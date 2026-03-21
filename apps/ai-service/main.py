from flask import Flask, request, jsonify
import openai
import os

app = Flask(__name__)

@app.route('/ask', methods=['POST'])
def ask():
    data = request.json
    # OpenAI implementation here
    return jsonify({"answer": "I am the UniVerse AI assistant."})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
