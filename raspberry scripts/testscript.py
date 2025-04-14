import requests
import time
import sys
import json

# Replace with your computer's IP address and port
SERVER_IP = "192.106.101.104"  # CHANGE THIS to your computer's local IP address
PORT = 8080               # Change if your Spring Boot app uses a different port

TEST_ENDPOINT = f"http://{SERVER_IP}:{PORT}/api/test/ping"

def test_connection():
    print(f"Testing connection to: {TEST_ENDPOINT}")
    
    try:
        response = requests.get(TEST_ENDPOINT, timeout=5)
        
        # Print response status
        print(f"Status code: {response.status_code}")
        
        if response.status_code == 200:
            print("Connection successful!")
            print("Response body:")
            try:
                if response.text.strip():  # Check if response has content
                    json_response = response.json()
                    print(json_response)
                else:
                    print("(Empty response)")
                return True
            except json.JSONDecodeError:
                print(f"Raw response (not valid JSON): '{response.text}'")
                return True
        else:
            print(f"Received error status code: {response.status_code}")
            print("Response body:")
            print(response.text)
            return False
            
    except requests.exceptions.ConnectionError:
        print("Connection error - Failed to connect to the server.")
        print("Please check the following:")
        print("1. Your server is running")
        print(f"2. The IP address ({SERVER_IP}) is correct")
        print(f"3. Your computer's firewall allows connections on port {PORT}")
        print("4. Both devices are on the same network")
        return False
        
    except requests.exceptions.Timeout:
        print("Connection timeout - Server too slow to respond.")
        return False
        
    except requests.exceptions.RequestException as e:
        print(f"Request error: {e}")
        return False
        
    except Exception as e:
        print(f"Unexpected error: {e}")
        return False

def main():
    print("Raspberry Pi to Server Connection Test")
    print("-" * 40)
    
    # Test the connection once immediately
    success = test_connection()
    
    # If command line argument 'loop' is provided, keep testing every 5 seconds
    if len(sys.argv) > 1 and sys.argv[1] == "loop":
        try:
            while True:
                print("\nWaiting 5 seconds before next test...")
                time.sleep(5)
                test_connection()
        except KeyboardInterrupt:
            print("\nTest loop stopped by user.")
    
    # Return success status as exit code
    return 0 if success else 1

if __name__ == "__main__":
    sys.exit(main())
