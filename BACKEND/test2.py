from distutils.command.sdist import sdist
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC #selenium에서 사용할 모듈 import

import time
import requests
from bs4 import BeautifulSoup
import re
import csv
from sklearn.utils import indices_to_mask

from sqlalchemy import false
from sympy import true
driver = webdriver.Chrome("C:/scraping/chromedriver.exe") #selenium 사용에 필요한 chromedriver.exe 파일 경로 지정

#driver.get("https://map.naver.com/v5/") #네이버 신 지도 
driver.get("https://map.naver.com/v5/directions/14126072.186824333,4506659.900486704,%EC%B6%9C%EB%B0%9C%EC%A7%80%EC%9D%B4%EB%A6%84,,ADDRESS_POI/14140651.845248867,4508743.779035402,%EB%8F%84%EC%B0%A9%EC%A7%80%EC%9D%B4%EB%A6%84,,ADDRESS_POI/-/transit/1?c=14126306.1581301,4506797.5334312,16,0,0,0,dh") #네이버 신 지도 
try:
   element = WebDriverWait(driver, 10).until(
       EC.presence_of_element_located((By.CLASS_NAME, "input_search"))
   ) #입력창이 뜰 때까지 대기
finally:
   pass
print("입력창이 뜸!!!")
time.sleep(3) #화면 표시 기다리기

all_elements = driver.find_elements(By.CSS_SELECTOR, ".directions_scroll_area.ng-star-inserted > .scroll_inner > .search_result_list > .ng-star-inserted")

for detail in all_elements:
    print("--------------------")
    elements = detail.find_elements(By.CSS_SELECTOR, ".timeline_item_list > .timeline_item > .timeline_content > .time > .value.ng-star-inserted")
    class_elements = detail.find_elements(By.CSS_SELECTOR, ".timeline_item_list > .timeline_item > .icon_area > .blind")
    label_elements = detail.find_elements(By.CSS_SELECTOR, ".timeline_item_list > .timeline_item > .icon_area > .label")
    public_transports = detail.find_elements(By.CSS_SELECTOR, ".step_info_area > .step_title_area > .step_title.ng-star-inserted")
    total = detail.find_elements(By.CSS_SELECTOR, ".timebar.summary.ng-star-inserted > .timeline_list_wrap.ng-star-inserted > .timeline_item_list > .timeline_item")
    
    indicator_of_walk = total[len(total)-1].find_element(By.CSS_SELECTOR,".icon_area > .blind") #이 값이 ""이면 도보, 아니면 "지하철" 또는 "버스" 값임.

    len_public_transports = len(public_transports)
    stations = detail.find_elements(By.CSS_SELECTOR, ".step_info_area >.step_title_area > .step_title")
    end_station = stations[len_public_transports]
    cnt = -1    # cnt + 1 => 타고간 대중교통의 수(현재는 0개)
    for value, value_1, value_2 in zip(elements, class_elements, label_elements):
        is_public_transports = 0
        if (value_1.text == ""):
            a = "도보"
            b = "도보"
        else:
            a = value_1.text
            is_public_transports = 1
            b = value_2.text
            cnt += 1
        if (is_public_transports == 1):
            print(value.text, a, b, public_transports[cnt].text)
            if (indicator_of_walk != ""):
                print("하차역", end_station.text)
        elif (len_public_transports == (cnt+1)):
            print(value.text, a, b, end_station.text, "마지막!!!")
        else:
            print(value.text, a, b)
"""
abc = driver.find_elements(By.CSS_SELECTOR, ".step_info_area > .step_title_area > .step_title.ng-star-inserted")

for value in abc:
    print(value.text)
print(len(abc))
print("끝")
"""
"""
elements = driver.find_elements(By.CSS_SELECTOR, ".timeline_item_list > .timeline_item > .timeline_content > .time > .value.ng-star-inserted")
for value in elements:
    print(value.text)
class_elements = driver.find_elements(By.CSS_SELECTOR, ".timeline_item_list > .timeline_item > .icon_area > .blind")
label_elements = driver.find_elements(By.CSS_SELECTOR, ".label")


print(len(elements) == len(class_elements))
print(len(elements))
print(len(class_elements))
print("class elements\n")
for value in class_elements:
    if (value.text == ""):
        print(value.text == "")
    else:
        print(value.text)
    
    
print("label elements\n")
for value in label_elements:
    print(value.text)
#print(driver.find_elements(By.CSS_SELECTOR, ".value.ng-star-inserted").text)
"""
time.sleep(100000)