import json
import uuid
import random

#ID
def generate_unique_id():
    return str(uuid.uuid4())

#Price se manca
def generate_random_price():
    return random.randint(10, 100)

#Alcol se manca
def generate_random_alcohol_percentage():
    return str(random.randint(12, 18)) + '%'

#Originale
with open('updated_wines.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

for wine in data:

    #Cambiamenti vivino
    if wine.get('provenance') == 'V':
        
        wine['variety'] = {
            1: "Red",
            2: "White",
            3: "Champagne"
        }.get(wine['type_id'], "Red")
        
        del wine['type_id']  

        if 'region' in wine and 'name' in wine['region']:
            wine['region'] = wine['region']['name']  

        if 'winery' in wine and 'status' in wine['winery']:
            del wine['winery']['status']  

    #Cambiamenti winemag
    elif wine.get('provenance') == 'W':

        if 'title' in wine:
            wine['name'] = wine['title']  
            del wine['title']

        if 'region_1' in wine:
            wine['region'] = wine['region_1']
            del wine['region_1']

        if 'region_2' in wine:
            del wine['region_2']

        if 'taster_twitter_handle' in wine:
            del wine['taster_twitter_handle']

        if 'designation' in wine:
            del wine['designation']

with open('updated_wines_V2.json', 'w', encoding='utf-8') as file:
    json.dump(data, file, indent=4, ensure_ascii=False)

with open('updated_wines_V2.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

num_winemag = sum(1 for wine in data if wine.get('provenance') == 'W')
num_vivino = sum(1 for wine in data if wine.get('provenance') == 'V')

print(f"Numero di vini di Winemag: {num_winemag}")
print(f"Numero di vini di Vivino: {num_vivino}")
