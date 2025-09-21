import requests
import random
import string
import time
import re

# --- CONFIGURABLE ---
WAIT_TIME_SEC = 2
NUM_POLLS = 10

def rand_password(n=12):
    # mail.tm: at least 8 chars, 1 uppercase, 1 lowercase, 1 digit
    while True:
        pw = [
            random.choice(string.ascii_uppercase),
            random.choice(string.ascii_lowercase),
            random.choice(string.digits),
        ]
        pw += random.choices(string.ascii_letters + string.digits, k=n-3)
        random.shuffle(pw)
        password = ''.join(pw)
        # Double check it always fits
        if (any(c.islower() for c in password) and
            any(c.isupper() for c in password) and
            any(c.isdigit() for c in password)):
            return password

def rand_str(n=10):
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=n))

def get_mail_tm_domain():
    resp = requests.get("https://api.mail.tm/domains")
    resp.raise_for_status()
    return resp.json()["hydra:member"][0]["domain"]

def create_mail_tm_account(email, password):
    resp = requests.post(
        "https://api.mail.tm/accounts",
        json={"address": email, "password": password}
    )
    if resp.status_code == 201:
        return True
    elif "exists" in resp.text:
        return False
    else:
        raise Exception(f"Failed to create account: {resp.text}")

def login_mail_tm(email, password):
    resp = requests.post(
        "https://api.mail.tm/token",
        json={"address": email, "password": password}
    )
    resp.raise_for_status()
    return resp.json()["token"]

def fetch_emails(token):
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.get("https://api.mail.tm/messages", headers=headers)
    resp.raise_for_status()
    return resp.json()["hydra:member"]

def get_email_content(token, message_id):
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.get(f"https://api.mail.tm/messages/{message_id}", headers=headers)
    resp.raise_for_status()
    return resp.json()

def extract_verification_codes(text):
    return re.findall(r"\b\d{6}\b", text)

# --- MAIN FLOW ---

domain = get_mail_tm_domain()
while True:
    email = f"{rand_str(10)}@{domain}"
    password = rand_password(12)
    if create_mail_tm_account(email, password):
        break
print(f"\nYour disposable email address is: \033[92m{email}\033[0m")
print(f"(Password: {password})")
print("\nUse this email for signup, then press Enter to fetch emails and scan for codes.")
input("Press Enter when ready...")

token = login_mail_tm(email, password)

print("\nChecking for new emails...\n")
codes_found = False
for poll in range(NUM_POLLS):
    messages = fetch_emails(token)
    if messages:
        for msg in messages:
            msg_full = get_email_content(token, msg["id"])
            body = msg_full.get("text", "") + "\n" + msg_full.get("html", "")
            codes = extract_verification_codes(body)
            print(f"\n--- Message from: {msg_full['from']['address']} ---")
            print("Subject:", msg_full.get("subject"))
            if codes:
                codes_found = True
                print(f"\033[93mVerification code(s) found: {codes}\033[0m")
            else:
                print("No code found.")
            print("Preview:", (body[:300] + '...') if len(body) > 300 else body)
            print("-" * 40)
        break
    else:
        time.sleep(WAIT_TIME_SEC)
        print(f"Polling... ({poll+1}/{NUM_POLLS}) No emails yet.")
if not codes_found:
    print("\nNo verification code found. Try again or increase NUM_POLLS/WAIT_TIME_SEC if needed.")

