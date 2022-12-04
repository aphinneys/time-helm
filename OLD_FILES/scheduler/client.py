from typing import Optional
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
import os.path as path

from datetime import datetime

SCOPES = ['https://www.googleapis.com/auth/calendar.readonly']


class CalendarClient:
    def __init__(self) -> None:
        self._creds: Optional[Credentials] = None
        # The file token.json stores the user's access and refresh tokens, and is
        # created automatically when the authorization flow completes for the first
        # time.
        if path.exists('token.json'):
            self._creds = Credentials.from_authorized_user_file(
                'token.json', SCOPES)
        # If there are no (valid) credentials available, let the user log in.
        if not self._creds or not self._creds.valid:
            if self._creds and self._creds.expired and self._creds.refresh_token:
                self._creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(
                    'credentials.json', SCOPES)
                self._creds = flow.run_local_server(port=0)
            # Save the credentials for the next run
            with open('token.json', 'w') as token:
                token.write(self._creds.to_json())

        self.service = build('calendar', 'v3', credentials=self._creds)

    def get_schedule(self):
        try:
            # Call the Calendar API
            now = datetime.utcnow().isoformat() + 'Z'  # 'Z' indicates UTC time
            print('Getting the upcoming 10 events')
            events_result = self.service.events()\
                .list(calendarId='primary', timeMin=now,
                      maxResults=10, singleEvents=True,
                      orderBy='startTime').execute()
            events = events_result.get('items', [])

            if not events:
                print('No upcoming events found.')
                return

            # Prints the start and name of the next 10 events
            for event in events:
                start = event['start'].get(
                    'dateTime', event['start'].get('date'))
                print(start, event['summary'])

        except HttpError as error:
            print('An error occurred:', error)
