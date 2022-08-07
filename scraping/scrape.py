from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import pandas as pd

driver = webdriver.Chrome('chromedriver')
driver.get('https://fantasy.espn.com/football/players/add?leagueId=1589782588')

players = driver.find_elements_by_xpath('//a[@class="AnchorLink link clr-link pointer"]')
position = driver.find_elements_by_xpath('//span[@class="playerinfo__playerpos ttu"]')
players_list = []
position_list = []
for p in range(len(players)):
    players_list.append(players[p].text)
    position_list.append(position[p].text)
print(players_list)