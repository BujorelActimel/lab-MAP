# Retea de Socializare - Proiect MAP

## Descriere
Implementarea unei retele de socializare cu model simplificat, din care sa poata fi extrase informatii relevante. Proiectul face parte din laboratorul de Metode Avansate de Programare.

## Cerinte Functionale (5 puncte)

### Gestiunea Utilizatorilor (2p)
- Adaugare utilizator
- Stergere utilizator
- Operatii CRUD de baza
- Date citite initial dintr-un fisier CSV

### Gestiunea Prieteniilor (2p)
- Adaugare prieten
- Stergere prieten
- Gestionarea relatiilor intre utilizatori

### Analiza Retelei (1p)
- Determinarea numarului de comunitati (componente conexe) (0.5p)
- Identificarea celei mai sociabile comunitati (componenta conexa cu cel mai lung drum) (0.5p)

### Functionalitati Aditionale
- Gestiunea mesajelor: trimitere/stergere
- Adaugare obiecte de tip Page
- Gestiunea evenimentelor cu sablon Observer pentru notificari
- Abonare/dezabonare la evenimente
- Autentificare: logare/delogare
- Vizualizare istoric evenimente pe perioada calendaristica
- Vizualizare istoric utilizatori pe perioada calendaristica

## Cerinte Non-Functionale (4 puncte)

### Arhitectura (1.25p)
- Arhitectura stratificata (0.5p)
- Domain Driven Design (DDD) (0.25p)
- Documentatie JavaDoc (0.5p)

### Date si Validare (1.5p)
- Persistenta datelor in memorie sau fisier (1p)
- Validarea datelor folosind Strategy Pattern (0.5p)

### Alte Cerinte (1.25p)
- Clase proprii de exceptii pentru tratarea situatiilor speciale (0.25p)
- Interfata cu utilizatorul de tip consola (minimala) (0.5p)
- Test cases pentru metodele implementate (0.5p)

## Cum sa Rulati Proiectul
1. Clonati repository-ul
2. Asigurati-va ca aveti Java 17 si Maven instalate
3. In directorul proiectului, rulati:
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="Main"
```
