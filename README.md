# 📻 Radio System

Aplikacija omogoča uporabnikom prijavo, registracijo, iskanje radijskih postaj in komentiranje le-teh. Skrbniki sistema lahko spremljajo komentarje in podatke o radijskih postajah.

## Vsebina

- [Opis aplikacije](#opis-aplikacije)
- [Struktura podatkovne baze](#struktura-podatkovne-baze)
- [SQL funkcije](#sql-funkcije)
- [SQL sprožilci (triggerji)](#sql-sprožilci-triggerji)
- [Uporaba v Java aplikaciji](#uporaba-v-java-aplikaciji)

## Opis aplikacije

Uporabnik se lahko:

- registrira,
- prijavi,
- pregleda seznam radijskih postaj,
- pregleda podrobnosti določene radijske postaje,
- komentira posamezno postajo,
- pregleda komentarje drugih uporabnikov.

Skrbniki baze imajo funkcije za spremljanje štetja komentarjev in časa zadnje spremembe.

## Struktura podatkovne baze

### Tabele:

- **drzave**(id, ime, koda)
- **kraj**(id, ime, postna_st, drzava_id)
- **users**(id, username, password, email, phone, kraj_id, created_at, updated_at)
- **radio**(id, ime, frekvenca, channel, valid_until, phone, email, kraj_id, comment_count, last_modified)
- **comments**(id, comment_text, frequency_id, user_id, created_at)

### Relacije:

- `radio.kraj_id → kraj.id`
- `kraj.drzava_id → drzave.id`
- `users.kraj_id → kraj.id`
- `comments.frequency_id → radio.id`
- `comments.user_id → users.id`

## SQL funkcije

### Avtentikacija

#### `check_user_validity(p_username TEXT, p_password TEXT) RETURNS BOOLEAN`

Preveri, ali obstaja uporabnik s podanim uporabniškim imenom in geslom.

```sql
CREATE OR REPLACE FUNCTION check_user_validity(p_username TEXT, p_password TEXT)
RETURNS BOOLEAN AS $$
DECLARE
    user_exists BOOLEAN;
BEGIN
    SELECT EXISTS(SELECT 1 FROM users WHERE username = p_username AND password = p_password)
    INTO user_exists;
    RETURN user_exists;
END;
$$ LANGUAGE plpgsql;
```

#### `prijava_uporabnika(p_username TEXT, p_password TEXT) RETURNS INTEGER`

Vrne ID uporabnika, če obstaja, sicer -1.

```sql
CREATE OR REPLACE FUNCTION prijava_uporabnika(p_username TEXT, p_password TEXT)
RETURNS INTEGER AS $$
DECLARE
    user_id INTEGER;
BEGIN
    SELECT id INTO user_id FROM users WHERE username = p_username AND password = p_password;

    IF user_id IS NULL THEN
        RETURN -1;
    ELSE
        RETURN user_id;
    END IF;
END;
$$ LANGUAGE plpgsql;
```

### Registracija

#### `register_new_user(p_username TEXT, p_password TEXT) RETURNS VOID`

Registrira novega uporabnika brez preverjanja unikatnosti.

```sql
CREATE OR REPLACE FUNCTION register_new_user(p_username TEXT, p_password TEXT)
RETURNS VOID AS $$
BEGIN
    INSERT INTO users (username, password) VALUES (p_username, p_password);
END;
$$ LANGUAGE plpgsql;
```

#### `registracija_uporabnika(p_username TEXT, p_password TEXT) RETURNS BOOLEAN`

Registrira novega uporabnika, če uporabniško ime še ne obstaja.

```sql
CREATE OR REPLACE FUNCTION registracija_uporabnika(p_username TEXT, p_password TEXT)
RETURNS BOOLEAN AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username) THEN
        RETURN FALSE;
    END IF;

    INSERT INTO users (username, password)
    VALUES (p_username, p_password);

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
```

### Radijske postaje

#### `pridobi_radio_id(po_ime TEXT) RETURNS INTEGER`

Vrne ID radijske postaje glede na ime.

```sql
CREATE OR REPLACE FUNCTION pridobi_radio_id(po_ime TEXT)
RETURNS INTEGER AS $$
DECLARE
    rid INT;
BEGIN
    SELECT id INTO rid FROM radio WHERE ime = po_ime;
    RETURN rid;
END;
$$ LANGUAGE plpgsql;
```

#### `pridobi_podrobnosti_radia(po_ime TEXT) RETURNS TABLE(...)`

Vrne podrobnosti radijske postaje glede na ime.

```sql
CREATE OR REPLACE FUNCTION pridobi_podrobnosti_radia(po_ime TEXT)
RETURNS TABLE(id INTEGER, ime TEXT, frekvenca DOUBLE PRECISION, channel TEXT, valid_until DATE, phone TEXT, email TEXT)
AS $$
BEGIN
    RETURN QUERY
    SELECT id, ime, frekvenca, channel, valid_until, phone, email
    FROM radio
    WHERE ime = po_ime;
END;
$$ LANGUAGE plpgsql;
```

#### `pridobi_vse_radio_postaje() RETURNS TABLE(id, ime, frekvenca)`

Vrne seznam vseh radijskih postaj.

```sql
CREATE OR REPLACE FUNCTION pridobi_vse_radio_postaje()
RETURNS TABLE(id INTEGER, ime VARCHAR, frekvenca DOUBLE PRECISION)
AS $$
BEGIN
    RETURN QUERY
    SELECT id, ime, frekvenca FROM radio;
END;
$$ LANGUAGE plpgsql;
```

### Komentarji

#### `dodaj_komentar(p_comment_text TEXT, p_frequency_id INT, p_user_id INT) RETURNS VOID`

Doda komentar za izbrano postajo.

```sql
CREATE OR REPLACE FUNCTION dodaj_komentar(p_comment_text TEXT, p_frequency_id INTEGER, p_user_id INTEGER)
RETURNS VOID AS $$
BEGIN
    INSERT INTO comments (comment_text, frequency_id, user_id, created_at)
    VALUES (p_comment_text, p_frequency_id, p_user_id, CURRENT_TIMESTAMP);
END;
$$ LANGUAGE plpgsql;
```

#### ==> `count_comments(radio_id INTEGER) RETURNS INTEGER`

Vrne število komentarjev za dano postajo.

```sql
CREATE OR REPLACE FUNCTION count_comments(radio_id INTEGER)
RETURNS INTEGER AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM comments WHERE frequency_id = radio_id);
END;
$$ LANGUAGE plpgsql;
```

### Spremljanje sprememb

#### `update_comment_count() RETURNS TRIGGER`

Posodobi `comment_count` v tabeli `radio` ob vsakem novem komentarju.

```sql
CREATE OR REPLACE FUNCTION update_comment_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE radio
    SET comment_count = (
        SELECT COUNT(*) FROM comments WHERE frequency_id = NEW.frequency_id
    )
    WHERE id = NEW.frequency_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

#### `update_radio_last_modified() RETURNS TRIGGER`

Posodobi `last_modified` polje pri novi objavi komentarja.

```sql
CREATE OR REPLACE FUNCTION update_radio_last_modified()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE radio
    SET last_modified = CURRENT_TIMESTAMP
    WHERE id = NEW.frequency_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

#### `update_last_modified() RETURNS TRIGGER`

Posodobi `last_modified` ob ročni posodobitvi vrstice v `radio`.

```sql
CREATE OR REPLACE FUNCTION update_last_modified()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

---

Vse funkcije so napisane v PL/pgSQL jeziku in skrbijo za logiko uporabnikov, komentarjev ter radijskih postaj.
