import scrapy
import re
import time
from datetime import datetime
from urllib.parse import urljoin
from scrapy import signals
from pydispatch import dispatcher


class VivinoSearchSpider(scrapy.Spider):
    name = "vivino_search"
    allowed_domains = ["www.vivhttps://www.vivino.com/explore?e=eJwtyk0KgCAQQOHbzLrMaDXLLhC0iohJK4TUsOnv9km2-hbv2YA5WONQROjGMgP1oGFQWLcNbLEuM54UzMS0gg8a9bQr8OODgdi4ZR-UPxzDxV0f7w-RKBIyUf1NvtD-Jr8%3Dino.com"]
    start_urls = [""]

    counter = 1
    n_pages = 1000
    nexts = start_urls

    def parse(self, response):
        link = response.url
        yield {
                "page_link" : link
            }
        print(f"Page : {self.counter}/{self.n_pages}")
        if self.counter < self.n_pages :
            link = "https://www.vivino.com" + response.css('.explorerPagination-module__next--1Z52y a::attr(href)').get()
            self.nexts.append(link)
            self.counter = self.counter + 1
            yield scrapy.Request(link, callback= self.parse)
    
        