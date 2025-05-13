# 📻 Mini projekt: Radio Komentarji

**Dokumentacija za mini projekt**

Ta dokument opisuje funkcionalnosti PostgreSQL baze in Java aplikacije, ki skupaj tvorita mini sistem za prikaz radijskih postaj ter komentiranje le-teh s strani uporabnikov.

---

## 📦 Vsebina

- [📝 Opis aplikacije](#-opis-aplikacije)
- [🗃️ Struktura podatkovne baze](#-struktura-podatkovne-baze)
- [🛠️ SQL funkcije](#-sql-funkcije)
- [⚙️ SQL sprožilci (triggerji)](#-sql-sprožilci-triggerji)
- [💻 Uporaba v Java aplikaciji](#-uporaba-v-java-aplikaciji)
- [ℹ️ Opombe](#️-opombe)
- [📌 Avtorstvo](#-avtorstvo)

---

## 📝 Opis aplikacije

Uporabnik lahko:

- se registrira,
- se prijavi,
- pregleda seznam radijskih postaj,
- pregleda podrobnosti določene radijske postaje,
- komentira posamezno postajo,
- pregleda komentarje drugih uporabnikov.

Skrbniki baze imajo funkcije za spremljanje štetja komentarjev in časa zadnje spremembe.

---

## 🗃️ Struktura podatkovne baze

### Tabele

- `drzave(id, ime, koda)`
- `kraj(id, ime, postna_st, drzava_id)`
- `users(id, username, password, email, phone, kraj_id, created_at, updated_at)`
- `radio(id, ime, frekvenca, channel, valid_until, phone, email, kraj_id, comment_count, last_modified)`
- `comments(id, comment_text, frequency_id, user_id, created_at)`

### Relacije

- `radio.kraj_id → kraj.id`
- `kraj.drzava_id → drzave.id`
- `users.kraj_id → kraj.id`
- `comments.frequency_id → radio.id`
- `comments.user_id → users.id`

---

## 🛠️ SQL funkcije

### 🔐 Avtentikacija

```sql
check_user_validity(p_username TEXT, p_password TEXT) RETURNS BOOLEAN
