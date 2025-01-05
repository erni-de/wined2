import json

def process_winemag(filename):
    with open(filename, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # Controlla che data sia una lista e non vuota
    if not isinstance(data, list) or not data:
        return []

    for doc in data:
        if 'reviews' in doc:
            doc.pop('reviews')
        doc['provenance'] = 'W'
        
    return data

def process_vivino(filename):
    """
    Legge il file Vivino, assumendo che sia una lista di oggetti ciascuno con la chiave 'wines',
    rimuove il campo 'reviews' da ogni vino e aggiunge 'provenance' = 'V' a ogni documento.
    """
    with open(filename, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    all_wines = []  # Lista per accumulare tutti i vini processati
    
    # Controlla che data sia una lista e non vuota
    if not isinstance(data, list) or not data:
        return []

    for element in data:
        # Assicurati che ogni elemento contenga la chiave 'wines'
        wines = element.get('wines', [])
        for wine in wines:
            if 'reviews' in wine:
                wine.pop('reviews')
            wine['provenance'] = 'V'
            all_wines.append(wine)
    
    return all_wines

def main():
    winemag_file = 'winemag_data.json'
    vivino_file = 'vivino_data.json'
    
    print("Processando winemag")
    winemag_docs = process_winemag(winemag_file)
    print("Processndo vivino")
    vivino_docs = process_vivino(vivino_file)
    
    combined_docs = winemag_docs + vivino_docs
    
    with open('wines.json', 'w', encoding='utf-8') as out_f:
        json.dump(combined_docs, out_f, indent=4, ensure_ascii=False)

if __name__ == '__main__':
    main()
