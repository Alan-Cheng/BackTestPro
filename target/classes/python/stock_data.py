import requests
import pandas as pd
import json
from datetime import datetime, timedelta
import sys

def fetch_stock_data(symbol, start_date, end_date):
    start_date = datetime.strptime(start_date, '%Y-%m-%d')
    end_date = datetime.strptime(end_date, '%Y-%m-%d')

    StockData = pd.DataFrame()

    while start_date <= end_date:
        # 添加每个月的第一天到列表中
        StockData = pd.concat([StockData, twse_api_request(symbol, start_date.strftime('%Y%m01'))], ignore_index=True)
        # 将日期移动到下一个月的第一天
        if start_date.month == 12:
            start_date = start_date.replace(year=start_date.year + 1, month=1, day=1)
        else:
            start_date = start_date.replace(month=start_date.month + 1, day=1)
    
    json_data = StockData.to_json(orient='records')
    json_data = json.loads(json_data)
    json_string = json.dumps(json_data, ensure_ascii=False)  # 使用双引号
    print(json_string)


def clean_and_convert(column):
    # 先用正则表达式去掉逗号和无效字符
    column = column.replace({',': '', 'X': ''}, regex=True)
    # 将非数字字符串替换为 NaN
    column = pd.to_numeric(column, errors='coerce')
    return column


def twse_api_request(symbol, date):
    html = requests.get('https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=%s&stockNo=%s' % (date,symbol))
    content = json.loads(html.text)
    stock_data = content['data']
    col_name = content['fields']
    df = pd.DataFrame(stock_data, columns=col_name)

    #處理資料中的逗號與小數點
    columns_to_clean = [
    "成交股數",
    "成交金額",
    "開盤價",
    "最高價",
    "最低價",
    "收盤價",
    "漲跌價差",
    "成交筆數"
    ]
    df[columns_to_clean] = df[columns_to_clean].apply(clean_and_convert)

    return df


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print(json.dumps({"error": "Usage: python fetch_stock_data.py <symbol> <start_date> <end_date>"}))
    else:
        symbol = sys.argv[1]
        start_date = sys.argv[2]
        end_date = sys.argv[3]
        fetch_stock_data(symbol, start_date, end_date)
