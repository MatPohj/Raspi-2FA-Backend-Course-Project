#!/usr/bin/env python3

import RPi.GPIO as GPIO
from mfrc522 import SimpleMFRC522
import requests
import time
import argparse
import sys

# Parse arguments
parser = argparse.ArgumentParser(description='NFC Authentication Client')
parser.add_argument('--server', default='https://raspi-2fa-backend-project-9d7fc3bfc347.herokuapp.com/ ', #or local ip of the machine running the spring boot application http://192.168.101.104:8080 
                    help='Server URL')
parser.add_argument('--username', required=True, 
                    help='Username to authenticate')
parser.add_argument('--session', required=True, 
                    help='Session ID to link verification')
args = parser.parse_args()

# Initialize the reader
reader = SimpleMFRC522()

# Endpoint URL
VALIDATE_URL = f"{args.server}/api/nfc/validate"

def validate_nfc(tag_id, username):
    """Send NFC tag to server for validation"""
    print(f"Validating tag {tag_id} for user {username}...")
    try:
        response = requests.post(
            VALIDATE_URL,
            json={"tagId": str(tag_id), "username": username, "sessionId": args.session},
            timeout=5
        )

        if response.status_code == 200:
            result = response.json()
            print(f"Full response: {response.json()}")
            print(f"Using session ID: {args.session}")
            if result.get("valid", False):
                print("✅ Access granted!")
                return True
            else:
                print("❌ Access denied: Invalid tag or wrong session id")
        else:
            print(f"❌ Server error: {response.status_code}")

    except Exception as e:
        print(f"❌ Connection error: {e}")
    
    return False

def main():
    print(f"NFC Authentication for {args.username}")
    print("=" * 40)
    print("Place your NFC tag on the reader...")
    
    try:
        while True:
            # Read tag
            tag_id, text = reader.read()
            print(f"Tag detected! ID: {tag_id}")

            # Validate with server
            access_granted = validate_nfc(tag_id, args.username)
            
            # Exit if access is granted
            if access_granted:
                print("Authentication successful! Exiting...")
                break
                
            # Wait before next read
            time.sleep(2)
            print("\nPlace your NFC tag on the reader...")

    except KeyboardInterrupt:
        print("\nExiting...")
    finally:
        GPIO.cleanup()

if __name__ == "__main__":
    main()