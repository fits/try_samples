
import sys
import requests
from bs4 import BeautifulSoup

url = sys.argv[1]

r = requests.get(url)

soup = BeautifulSoup(r.text, 'html.parser')

for a in soup.select('h3.r a'):
    print(f'{a.get_text()}, {a["href"]}')
