# File: my_app/main.py
from utils import string_helpers

if __name__ == "__main__":
    greeting = "hello world"
    capitalized_greeting = string_helpers.capitalize_string(greeting)
    print(capitalized_greeting) # Output: Hello world