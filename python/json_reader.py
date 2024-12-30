import json

# Nome del file JSON da cui leggere i dati
json_file = 'winemag-data-130k-v2.json'

# Apertura del file e caricamento del contenuto
with open(json_file, 'r', encoding='utf-8') as f:
    data = json.load(f)

# Itera su ogni record all'interno dei dati
for record in data:
    print("Country:", record.get('country', 'N/A'))
    print("Description:", record.get('description', 'N/A'))
    print("Designation:", record.get('designation', 'N/A'))
    print("Points:", record.get('points', 'N/A'))
    print("Price:", record.get('price', 'N/A'))
    print("Province:", record.get('province', 'N/A'))
    print("Region_1:", record.get('region_1', 'N/A'))
    print("Region_2:", record.get('region_2', 'N/A'))
    print("Taster Name:", record.get('taster_name', 'N/A'))
    print("Taster Twitter Handle:", record.get('taster_twitter_handle', 'N/A'))
    print("Title:", record.get('title', 'N/A'))
    print("Variety:", record.get('variety', 'N/A'))
    print("Winery:", record.get('winery', 'N/A'))
    print("-" * 50)