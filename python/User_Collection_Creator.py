import json
import pandas as pd
import random

def generate_payment():

    #CARD NUMBER
    card_number = ''

    for i in range(16):
        number = random.randint(0,9)
        card_number = str(card_number) + str(number)
    
    #CVV generation
    CVV = random.randint(100,999)
    
    expire_date = ''

    #EXPIRE_DATE
    result = random.randint(1,12)
    
    if result < 10:
        expire_date = expire_date + '0' + str(result)  
    else:
        expire_date = expire_date + str(result)
    
    expire_date= expire_date + '/' + str(random.randint(2027, 2038))

    #Generating the document to append
    random_payment = {
        "card_number": card_number,
        "CVV": CVV,
        "expire_date": expire_date
    }

    return random_payment

def main():
    "Apertura del file dai dati"
    with open('users_generated.json', 'r') as file:
        data = json.load(file)

    count = 0

    "Scorro i document per verificare l'unicità"
    for document in data:

        #Campi da eliminare gestisco l'eccezione sotto
        try:
            document.pop("id")
            document.pop("website")
            document.pop("image")
        except KeyError:
            print("Errore nel rimuovere la key, c'è un documento senza uno dei campi")

        #Riassegno l'indirizzo come campo unico
        address = document["address"]
        document["address"] = address['street'] + " " + address['city'] + " " + address['zipcode'] + " " + address['country']

        #Assegno i ruoli degli utenti in una percentuale predefinita (in tutto sono 50000)
        if count < 45000:
            document["user_level"] = "REGULAR"
            count += 1
        elif count < 49998:
            document["user_level"] = "PREMIUM"
            count += 1
        else:
            document["user_level"] = "ADMIN"
            count += 1

        #Generating random payment for the user with the function
        document["payment"] = generate_payment()

    #Salvo il file e lo riapro con PANDAS per togliere i duplicati
    with open('users_cleaned.json', 'w') as newf:
        json.dump(data, newf, indent=4, ensure_ascii = False)

    #Apro il file come dataframe ora
    df = pd.read_json('users_cleaned.json')

    #Filtrare duplicati per email e nickname
    df_filtrato = df[~df['email'].duplicated(keep=False)]
    df_filtrato = df_filtrato[~df_filtrato['nickname'].duplicated(keep=False)]

    #Stampo il numero finale di utenti
    print("Il numero di utenti totali finali e' " + str(len(df_filtrato)))
    
    print("Il numero di utenti REGULAR è " + str(len(df_filtrato[df_filtrato["user_level"] == 'REGULAR'])))
    print("Il numero di utenti PREMIUM è " + str(len(df_filtrato[df_filtrato["user_level"] == 'PREMIUM'])))
    print("Il numero di utenti ADMIN è " + str(len(df_filtrato[df_filtrato["user_level"] == 'ADMIN'])))

    #Lo converto a JSON e lo devo salvare come JSON senno' si bugga con i caratteri speciali
    json_str = df_filtrato.to_json(orient='records', indent=4)

    data = json.loads(json_str)

    json_str_no_ascii = json.dumps(data, indent=4, ensure_ascii=False)

    # Salva il risultato finale in un file JSON
    with open('users_final.json', 'w', encoding='utf-8') as f:
        f.write(json_str_no_ascii)

if __name__ == '__main__':
    main()
