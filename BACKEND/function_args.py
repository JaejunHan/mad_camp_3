# function_args.py 파일

import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding = 'utf-8')
def getName(name, age):
    print (name + " : " + age)


print("http://map.naver.com/index.nhn?slng="+sys.argv[1]+"&slat=37.4830969&stext=출발지이름&elng=127.0276368&elat=37.4979502&etext=도착지이름&menu=route&pathType=1")

"""
if __name__ == '__main__':
    getName(sys.argv[1], sys.argv[2])

    """