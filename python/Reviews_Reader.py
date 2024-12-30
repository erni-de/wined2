import json

def display_reviews(reviews):
    """Display the text (note) and stars (rating) of reviews three at a time."""
    for i in range(0, len(reviews), 3):
        batch = reviews[i:i+3]
        for idx, review in enumerate(batch, start=1):
            text = review.get('note', 'No text available.')  # Testo della recensione
            stars = review.get('rating', 'No rating')  # Numero di stelle
            print(f"Review {i + idx}:")
            print(f"  Text: {text}")
            print(f"  Stars: {stars}")
            print("-" * 40)
        
        if i + 3 < len(reviews):
            proceed = input("Show more reviews? (Press Enter to continue, or 'n' to skip): ").strip().lower()
            if proceed == 'n':
                break

def main():
    try:
        # Decodifica il file JSON
        with open("../data/reviews/vivino/R_EN_FR/data_1156.json", 'r', encoding='utf-8') as file:
            data = json.load(file)  # Decodifica il contenuto in un dizionario

        wines = data.get('wines', [])  # Accedi ai dati JSON decodificati

        if not wines:
            print("No wines found in the file.")
            return

        for wine in wines:
            print("\n" + "=" * 40)
            print(f"Wine ID: {wine['wine_id']}")
            print(f"Wine Name: {wine['wine_name']}")
            print("Reviews:")

            reviews = wine.get('reviews', [])
            if reviews:
                display_reviews(reviews)
            else:
                print("No reviews available.")

            proceed = input("\nMove to the next wine? (Press Enter to continue, or 'n' to quit): ").strip().lower()
            if proceed == 'n':
                break

    except FileNotFoundError:
        print("File not found. Please check the file path and try again.")
    except json.JSONDecodeError:
        print("Error decoding JSON. Please check the file format.")

if __name__ == "__main__":
    main()
