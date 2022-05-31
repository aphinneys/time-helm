#!/usr/bin/env python3


import client


def schedule(c):
    c.get_schedule()


def main():
    # TODO: actuallly do stuff
    schedule(client.CalendarClient())


if __name__ == '__main__':
    main()
