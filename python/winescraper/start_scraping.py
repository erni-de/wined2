import subprocess

subprocess.run(['scrapy', 'crawl', 'vivino_search','-o','./winescraper/data_json/page_links.json'], capture_output=True, text=True)
subprocess.run(['scrapy', 'crawl', 'vivino_wines','-o','./winescraper/data_json/wine_links.json'], capture_output=True, text=True)
subprocess.run(['python3','./winescraper/sel.py'], capture_output=True, text=True)
