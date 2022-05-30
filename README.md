# time-helm
Time Helm
you must admit that is a cool name.

Problem Statement. 

Having control of one's resources is nice. Doing so effortlessly is even nicer. Detailed time tracking is like detailed money tracking. You can waste a lot of energy on it if you don't automate it. But having it done causes great peace of mind!!

Mint https://mint.intuit.com/ has pretty graphs and reports, you don't do anything it just shows them. 

Anyway so there are two components, planning and reflection. 

Planning: Time block v1 contains my first attempt at automatically scheduling work blocks. For now I want to stick to 3 categories: deep work, freeform/light work, and learning/reading. The script reads my calendar and my shared calendar with Liron, and schedules events during my free time on a dedicated Time Blocks calendar. It doesn't edit or make events on any other calendars, just to be safe. It worked by scheduling deep work whenever I have a maximum amount of time available, and admin/light work when I have minimal time available, and reading blocks in whatever time I'm not having meetings. It also accounts for commute time before and after peacock blue events which occur on campus. It worked quite well, my biggest complaint is it didn't schedule breaks and it often overloaded days(>8 hours work related stuff I consider too long in one day). I would want the
