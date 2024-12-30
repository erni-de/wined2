import requests
import json

#Creo 50000 utenti da collegare ai vini di winemag

#25000 DONNE
api_request_female = "https://fakerapi.it/api/v2/persons?_quantity=25000&_gender=female&_birthday_start=1960-01-01"

#25000 UOMINI
api_request_male = "https://fakerapi.it/api/v2/persons?_quantity=25000&_gender=male&_birthday_start=1960-01-01"

response_female = requests.get(api_request_female)
response_male = requests.get(api_request_male)

#Se 200 è andato tutto ok nell'HTTP
if response_female.status_code == 200 and response_male.status_code == 200:
    
    # Converto in JSON e aggiungo i campi del nickname e password
    data_female = response_female.json()
    data_male = response_male.json()

    users_fem = data_female['data']
    users_male = data_male['data']

    for user in users_fem:
        user['nickname'] = user['firstname'] + "_" + user['lastname']
        user['password'] = user['firstname'] + user['birthday'].split('-')[0]

    for user in users_male:
        user['nickname'] = user['firstname'] + "_" + user['lastname']
        user['password'] = user['firstname'] + user['birthday'].split('-')[0]

    #Salvataggio dei dati in un file JSON
    with open('male_users.json', 'w') as json_file:
        json.dump(data_male, json_file, indent=4)

    with open('female_users.json', 'w') as json_file:
        json.dump(data_female, json_file, indent=4)
