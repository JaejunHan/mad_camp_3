from distutils.command.sdist import sdist
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC #selenium에서 사용할 모듈 import
from datetime import datetime

import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding = 'utf-8')

import time
import requests
from bs4 import BeautifulSoup
import re
import csv
from sklearn.utils import indices_to_mask

from sqlalchemy import false
from sympy import true

now_hour = datetime.now().hour
next_hour = now_hour + 1
next_next_hour = next_hour + 1

if (now_hour == 22):
    next_next_hour = "05"
elif (now_hour == 23):
    next_hour = "05"
    next_next_hour = "06"
# 시간을 string으로 바꿔줌.
now_hour = str(now_hour)
next_hour = str(next_hour)
next_next_hour = str(next_next_hour)

# 시간을 5가 아니라 05 형태로 바꿔줌
if (len(now_hour) != 2):
    now_hour = "0"+now_hour
if (len(next_hour) != 2):
    next_hour = "0"+next_hour
if (len(now_hour) != 2):
    next_next_hour = "0"+next_next_hour



driver = webdriver.Chrome("C:/scraping/chromedriver.exe") #selenium 사용에 필요한 chromedriver.exe 파일 경로 지정

#driver.get("https://map.naver.com/v5/") #네이버 신 지도
url = "http://map.naver.com/index.nhn?slng="+sys.argv[1]+"&slat="+sys.argv[2]+"&stext="+sys.argv[3]+"&elng="+sys.argv[4]+"&elat="+sys.argv[5]+"&etext="+sys.argv[6]+"&menu=route&pathType=1"
driver.get(url) #네이버 신 지도 
try:
   element = WebDriverWait(driver, 10).until(
       EC.presence_of_element_located((By.CLASS_NAME, "input_search"))
   ) #입력창이 뜰 때까지 대기
finally:
   pass
#print("입력창이 뜸!!!")
time.sleep(2) #화면 표시 기다리기


all_elements = driver.find_elements(By.CSS_SELECTOR, ".directions_scroll_area.ng-star-inserted > .scroll_inner > .search_result_list > .ng-star-inserted")

all_elements_list = []
for detail in all_elements:
    detail_list = []
    click = detail.find_element(By.CSS_SELECTOR, ".timebar.summary.ng-star-inserted")
    click.click()   #상세보기 페이지로 넘어감

    #승차 / 하차 나타내는 것
    WebDriverWait(driver, 1).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".public_result_area.ng-star-inserted > .path_area > .list_path > .item_path.ng-star-inserted")))
    in_or_out = driver.find_elements(By.CSS_SELECTOR, ".public_result_area.ng-star-inserted > .path_area > .list_path > .item_path.ng-star-inserted")
    #print(len(in_or_out))
    WebDriverWait(driver, 1).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".path_name_box > .path_name > .path_name_text")))
    cnt = 0
    len_in_or_out = len(in_or_out)
    for cnt in range(len_in_or_out):
        in_or_out_dict = {}
        WebDriverWait(driver, 1).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".public_result_area.ng-star-inserted > .path_area > .list_path > .item_path.ng-star-inserted")))
        in_or_out = driver.find_elements(By.CSS_SELECTOR, ".public_result_area.ng-star-inserted > .path_area > .list_path > .item_path.ng-star-inserted")
        path = in_or_out[cnt]
        path_name = path.find_element(By.CSS_SELECTOR, ".path_name_box > .path_name > .path_name_text")
        try:    # *분 (소요 시간)
            # print(type(path_name.text)) #string
            time_spends = path.find_elements(By.CSS_SELECTOR, ".value.ng-star-inserted")
            total_time = 0
            cnt = 0
            for cnt in range(len(time_spends)):
                if (cnt == 0 and len(time_spends) == 2):
                    total_time += int(time_spends[cnt].text) * 60
                else:
                    total_time += int(time_spends[cnt].text)
            in_or_out_dict["time"] = str(total_time)
            # print(time_spend.text, end = ' ')
        except:
            print("", end= "")
        try:    # 이동수단, 지하철, 버스, 도보, ...
            transport = path.find_element(By.CSS_SELECTOR, ".ng-star-inserted > .icon.ng-star-inserted > .inner")
            if (transport.text == "이동수단" or transport.text == "도보"):
                in_or_out_dict["type"] = "0"
            elif (transport.text == "지하철"):
                in_or_out_dict["type"] = "1"
            elif (transport.text == "버스"):
                in_or_out_dict["type"] = "2"
            # print(transport.text, end=' ')
        except:
            in_or_out_dict["type"] = "3"    # 그냥 하차, 또는 도착(마지막에 있으면)
            #print("하차", end=' ')
        is_bus = 0
        try:    # 버스 번호(5535, 106, ...)
            bus_name = path.find_element(By.CSS_SELECTOR, ".bus > .name-text.ng-star-inserted")
            in_or_out_dict["bus_name"] = bus_name.text
            is_bus = 1
            #print(bus_name.text, end=' ')
        except:
            print("", end='')
        
        # 역, 승하차 (ex) 고용노동부관악지청.이마트구로점 승차)
        in_or_out_dict["place"] = path_name.text
        #print(path_name.text, end = " ")
        
        
        try:    # 버스의 경우
            if (is_bus == 0):
                raise Exception('버스가 아닙니다.')
            bus_next_list = path.find_elements(By.CSS_SELECTOR, ".next_bus_info_item.ng-star-inserted")
            len_bus_next_list = len(bus_next_list)
            for i in range(len_bus_next_list):
                next_time = bus_next_list[i].find_element(By.CSS_SELECTOR,".point")
                if (i == 0):
                    in_or_out_dict["next_time"] = next_time.text
                else:
                    in_or_out_dict["next_next_time"] = next_time.text
                #print(next_time.text, end = ' ')
            # 버스 노선에서 배차 간격 확인
            WebDriverWait(driver, 2).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".bus > .name-text.ng-star-inserted")))
            button_more = path.find_element(By.CSS_SELECTOR, ".bus > .name-text.ng-star-inserted")
            #print("배차간격", end = " ")
            button_more.click()
            WebDriverWait(driver, 2).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".list_bus_info.ng-star-inserted > .item_bus_info.ng-star-inserted > .info_value > .value.ng-star-inserted")))
            between_bus_time = driver.find_element(By.CSS_SELECTOR, ".list_bus_info.ng-star-inserted > .item_bus_info.ng-star-inserted > .info_value > .value.ng-star-inserted")
            was_digit = 0
            time_interval = ""
            time_interval_0 = ""
            cnt_interval = 0
            #print(between_bus_time.text)
            for char in between_bus_time.text:
                if (char.isdigit()):    # 만약에 숫자면
                    time_interval += char
                    if (was_digit == 0):  # 직전 문자가 숫자가 아니었다면
                        was_digit = 1
                else:   # 숫자가 아니면
                    if (was_digit == 1): # 직전 char이 숫자였다면
                        cnt_interval += 1
                        if (cnt_interval == 2): # interval이 2개 나오면
                            break
                        elif (cnt_interval == 1): # 첫번째 interval이면
                            time_interval_0 = time_interval
                            time_interval = ""
                    was_digit = 0
            if (cnt_interval == 2):
                time_interval = str(int((int(time_interval_0)+int(time_interval))/2))
            elif (cnt_interval == 1):
                time_interval = time_interval_0
            in_or_out_dict["interval"] = time_interval
            #print("time_interval = ", time_interval, end=" ")                        
            #print(between_bus_time.text, end = '')
            in_or_out = driver.find_elements(By.CSS_SELECTOR, ".public_result_area.ng-star-inserted > .path_area > .list_path > .item_path.ng-star-inserted")
            path = in_or_out[cnt]
            click.click()
        except:
            print("", end="")

        subway_direction_text = ""
        try:
            subway_direction = path.find_element(By.CSS_SELECTOR, ".direction.ng-star-inserted")
            in_or_out_dict["direction"] = subway_direction.text
            subway_direction_text = subway_direction.text[:-1]
            #print(subway_direction.text, end = "")
        except:
            print("", end = "")

        try:
            # 지하철 시간표 더보기 버튼
            button_subway = path.find_element(By.CSS_SELECTOR, ".info_btn_area.ng-star-inserted> .btn_transport_info")
            button_subway.click()
            WebDriverWait(driver, 2).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".schedule_time_21.ng-star-inserted")))
            subway_selector_0 = ".schedule_time_" + now_hour + ".ng-star-inserted"
            subway_selector_1 = ".schedule_time_" + next_hour + ".ng-star-inserted"
            subway_selector_2 = ".schedule_time_" + next_next_hour + ".ng-star-inserted"
            subways_0 = driver.find_element(By.CSS_SELECTOR, subway_selector_0)
            minutes_0_list = []
            minutes_1_list = []
            minutes_2_list = []
            minutes_0 = subways_0.find_elements(By.CSS_SELECTOR, ".item_schedule_inner > .item_schedule_box")
            for minute in minutes_0:
                if subway_direction_text in minute.text:
                    a = minute.find_element(By.CSS_SELECTOR, ".schedule_time").text
                    minutes_0_list.append(a)
                    #print(a)
            subways_1 = driver.find_element(By.CSS_SELECTOR, subway_selector_1)
            minutes_1 = subways_1.find_elements(By.CSS_SELECTOR, ".item_schedule_inner > .item_schedule_box")
            for minute in minutes_1:
                if subway_direction_text in minute.text:
                    b = minute.find_element(By.CSS_SELECTOR, ".schedule_time").text
                    minutes_1_list.append(b)
                    #print(b)
            subways_2 = driver.find_element(By.CSS_SELECTOR, subway_selector_2)
            minutes_2 = subways_2.find_elements(By.CSS_SELECTOR, ".item_schedule_inner > .item_schedule_box")
            for minute in minutes_2:
                if subway_direction_text in minute.text:
                    c = minute.find_element(By.CSS_SELECTOR, ".schedule_time").text
                    minutes_2_list.append(c)
                    #print(c)
            in_or_out_dict["timetable0"] = minutes_0_list
            in_or_out_dict["timetable1"] = minutes_1_list
            in_or_out_dict["timetable2"] = minutes_2_list
            in_or_out = driver.find_elements(By.CSS_SELECTOR, ".public_result_area.ng-star-inserted > .path_area > .list_path > .item_path.ng-star-inserted")
            path = in_or_out[cnt]
            click.click()
        except:
            print("", end="")
        #print("")
        detail_list.append(in_or_out_dict)
    all_elements_list.append(str(detail_list))
#send_json = {"jsonArray" : all_elements_list}
print(all_elements_list)
driver.quit()
"""
import json
with open('./data.json', 'w', encoding='UTF-8-sig') as f:
    json.dump(send_json, f, ensure_ascii=False)
"""