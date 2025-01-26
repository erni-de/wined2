import json

def aggiorna_wines_definitive(nuovi_doc_file, definitive_file):
    """
    Legge il file 'definitive_file' (es. wines_definitive.json) che contiene già dei documenti,
    ne costruisce un set di 'name' già presenti per evitare duplicati.
    
    Poi legge i nuovi documenti dal file 'nuovi_doc_file' (es. wines_merged.json).
    Per ogni nuovo documento, se 'name' non è già presente, lo aggiunge alla lista esistente.
    
    Infine riscrive l'intera lista su 'definitive_file'.
    """
    # 1) Carica i documenti già presenti in wines_definitive.json
    with open(definitive_file, 'r', encoding='utf-8') as f:
        docs_esistenti = json.load(f)

    # 2) Costruisce un set di tutti i name già presenti
    nomi_esistenti = set()
    for doc in docs_esistenti:
        nome = doc.get("name", None)
        if nome:
            nomi_esistenti.add(nome)

    # 3) Carica i nuovi documenti
    with open(nuovi_doc_file, 'r', encoding='utf-8') as f:
        nuovi_documenti = json.load(f)

    # 4) Per ogni nuovo documento, se il name non è già nel set, lo aggiunge
    count_aggiunti = 0
    for ndoc in nuovi_documenti:
        nome_nuovo = ndoc.get("name", None)
        if nome_nuovo and (nome_nuovo not in nomi_esistenti):
            docs_esistenti.append(ndoc)
            nomi_esistenti.add(nome_nuovo)
            count_aggiunti += 1

    # 5) Riscrive l'intero elenco nel file definitive_file
    with open(definitive_file, 'w', encoding='utf-8') as f:
        json.dump(docs_esistenti, f, indent=4, ensure_ascii=False)

    print(f"Operazione completata. Aggiunti {count_aggiunti} nuovi documenti a {definitive_file}.")


if __name__ == "__main__":
    # File dei nuovi documenti unificati
    file_nuovi_documenti = "wines_merged.json"
    # File 'definitivo' su cui aggiungere
    file_definitivo = "wines_definitive.json"

    aggiorna_wines_definitive(file_nuovi_documenti, file_definitivo)
    print("Aggiornamento di wines_definitive.json completato con successo.")
