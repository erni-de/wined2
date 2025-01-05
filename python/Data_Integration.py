import json

with open("../data/users/users_generated.json", 'r') as file:
    users = json.load(file)

print("Numero di utenti sintetici pari a " + str(len(users)))