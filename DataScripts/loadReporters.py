import requests as r
import json

HOSTNAME = '172.16.5.20'

with open('data\\clean\\reporters_clean.json') as fin:
    reporters = json.load(fin)

session = r.session()
login = session.post(f"http://{HOSTNAME}:8080/SocialNews/login?email={'f.cristofani@socialnews.it'}&password={'admin'}&accessType={'admin'}")

for reporter in reporters:
    res = session.post(f'http://{HOSTNAME}:8080/SocialNews/admin/addReporter', json.dumps(reporter))

session.close()