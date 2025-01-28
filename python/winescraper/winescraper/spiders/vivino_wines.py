import scrapy
import json

class VivinoWinesSpider(scrapy.Spider):
    name = "vivino_wines"
    allowed_domains = ["www.vivino.com"]
    start_urls = ["https://www.vivino.com"]

    def parse(self, response) :
        with open('/home/erni/scraping/v_folder/winescraper/winescraper/data_json/page_links.json', 'r') as file:
            data = json.load(file)

        for d in data :
            yield scrapy.Request(d['page_link'], callback = self.parse_links)
    

    def parse_links(self, response):
        links = response.css('div.wineCard__wineCard--2dj2T > div:nth-child(1) a::attr(href)').getall()
        for link in links:
            yield {
                "wine_link" : link 
            }


