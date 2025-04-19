import csv
import requests


def create_drug(row):
    payload = {"name": row[0], "form": row[1], "units": 5, "price": 10}
    response = requests.post(
        "http://localhost:8080/api/v1/drugs/fill",  # Use HTTPS instead of HTTP
        json=payload,
        headers={"Content-Type": "application/json"},
        verify=False,
    )
    print("Status Code:", response.status_code)
    print("Response Text:", response.text)
    try:
        json_response = response.json()
        print(json_response)
    except requests.exceptions.JSONDecodeError:
        print("Invalid JSON response from server")


with open("drugs_info.csv") as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=",")
    for row in csv_reader:
        create_drug(row)
