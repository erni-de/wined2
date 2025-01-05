import requests
import json

#Funzione per generare utenti in batch
def generate_users_in_batches(total_users, gender, batch_size=1000):
    api_url = "https://fakerapi.it/api/v2/persons"
    users = []

    # Calcola il numero totale di batch necessari
    num_batches = (total_users // batch_size) + (1 if total_users % batch_size > 0 else 0)

    for batch in range(num_batches):
        #Calcola il numero di utenti da generare in questo batch
        current_batch_size = min(batch_size, total_users - len(users))
        print(f"Generazione batch {batch + 1}/{num_batches} per il genere {gender} ({current_batch_size} utenti)...")

        # Richiesta all'API
        response = requests.get(api_url, params={
            "_quantity": current_batch_size,
            "_gender": gender,
            "_birthday_start": "1960-01-01",
        })

        # Verifica della risposta
        if response.status_code == 200:
            data = response.json()
            users.extend(data["data"])
        else:
            print(f"Errore durante il batch {batch + 1}: {response.status_code}")
            break

    return users

#Funzione principale per generare utenti
def main():
    #Numero totale di utenti da generare per genere
    total_users_per_gender = 25000

    #Genera utenti femmine
    print("Generazione utenti femmine...")
    female_users = generate_users_in_batches(total_users_per_gender, "female")

    #Genera utenti maschi
    print("Generazione utenti maschi...")
    male_users = generate_users_in_batches(total_users_per_gender, "male")

    #Combina gli utenti maschi e femmine
    all_users = female_users + male_users

    #Aggiunge nickname e password
    print("Aggiunta di nickname e password...")
    for user in all_users:
        user["nickname"] = f"{user['firstname']}_{user['lastname']}"
        user["password"] = f"{user['firstname']}{user['birthday'].split('-')[0]}"

    #Salva tutti gli utenti in un unico file JSON
    output_file = "../data/users/users_generated.json"
    with open(output_file, "w", encoding="utf-8") as file:
        json.dump(all_users, file, indent=4, ensure_ascii=False)

    print(f"Generazione completata! Utenti totali generati: {len(all_users)}")
    print(f"File salvato in: {output_file}")

#Avvia la generazione
if __name__ == "__main__":
    main()
