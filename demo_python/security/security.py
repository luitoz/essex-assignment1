import sys
class Authenticator:

    """
    Class responsible for checking user credentials.
    Replace `valid_users` with a secure data source in a real application.
    """
    def __init__(self, debug):
        # Dummy user database for demonstration purposes
        self.valid_users = {
            "testUser": "testPassword", "testAdmin": "testPassword"
        }
        self.debug = debug
        self.user = None
        self.username = None
        self.password = None

    def login(self, username: str, password: str) -> bool:
        self.username = username
        self.password = password
        if self.debug:
            print(f"\tAuthenticating user: {username}")
            print(f"\tPassword: {password}")

        if self.valid_users.get(self.username) == self.password:
          if self.debug:
            print("\tLogin successful!")
          self.user = User(self.username)
          return True
        else:
          sys.stderr.write("Login failed.")
          self.username = None
          self.password = None
          return False


class User:
    def __init__(self, name):
        self.name = name

