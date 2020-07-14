import requests
import argparse
import random
import json
import time


class TerminalCommand:

    def get_argument(self):
        parser = argparse.ArgumentParser(description=
                                "***********************************\n "
                                " Crawl stock market data \n"
                                " ----------------------------\n"
                                " Parameters: \n"
                                "   --listed-company  (or -lc): Listed company name or stock symbol.\n"
                                "***********************************\n"
                                )

        parser.add_argument("-lc", type=str, dest="listed_company", default=None)
        parser.add_argument("--listed-company", type=str, dest="listed_company", default=None)
        return parser.parse_args()


class StockCrawler:

    http_header = {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36"}

    def stock_api(self, stock_symbol):
        return "http://www.tse.com.tw/exchangeReport/STOCK_DAY?response=json&date=20190101&stockNo={}".format(str(stock_symbol))

    def chk_data(self):
        pass


    def save_data(self):
        pass


    def send_http_request(self, api):
        response = requests.get(api, headers=self.http_header)
        if response.status_code == requests.codes.ok:
            return response
        else:
            return None


    def send_request(self, stock_symbol):
        # print("[API] Stock Symbol: ", stock_symbol)
        stock_api = self.stock_api(stock_symbol)
        # print("[API] Stock Market API: ", stock_api)
        crawl_result = self.send_http_request(stock_api)
        if crawl_result is not None:
            json_data = json.loads(crawl_result.text)
            crawl_result = {"stat": "SUCCESS", "stockInfo": json_data}
            print(crawl_result)
        else:
            crawl_result = {"stat": "FAIL", "stockInfo": None}
            print(crawl_result)

        time.sleep(random.randrange(random.randrange(1, 7), random.randrange(8, 24)))


if __name__ == '__main__':

    cmd_opt = TerminalCommand()
    args = cmd_opt.get_argument()

    stock_crawler = StockCrawler()
    stock_crawler.send_request(args.listed_company)
