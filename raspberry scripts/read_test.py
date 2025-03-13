#!/usr/bin/env python3

import RPi.GPIO as GPIO
from mfrc522 import SimpleMFRC522
import time

# Initialize the reader
reader = SimpleMFRC522()

print("NFC Reader Test")
print("==============")
print("Place an NFC tag near the reader...")

try:
    while True:
        print("\nWaiting for tag...")
        
        # Read the tag
        id, text = reader.read()
        
        print(f"Tag detected!")
        print(f"ID: {id}")
        print(f"Text: {text}")
        
        # Wait a moment before reading again
        time.sleep(2)
        
except KeyboardInterrupt:
    print("\nExiting...")
    
finally:
    # Clean up GPIO on exit
    GPIO.cleanup()
