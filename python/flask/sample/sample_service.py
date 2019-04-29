
from flask import Flask, request

app = Flask(__name__)

@app.route('/')
def call():
    name = request.args.get('name')
    return f'received-{name}'
