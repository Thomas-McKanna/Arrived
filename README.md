# Android Project - *Arrived*

Whenever I would go on a long trip away from home, my mother would tell me:

> Text me when you get there!

If I didn't, she would get worried - just think of all the awful things that could have happened! Wouldn't it be nice if you could tell your phone where you were going, and it would automatically text Mom, or whoever else, when you get there? Well, now you can!

**Arrived** is an Android app that allows a user to automatically send a text message to selected contacts upon arriving at a chosen destination. It allows you to:

- Choose your destination through the Google Places API
- Pick who to notify either through your phone contacts or manual entry
- Run the app in the background, so you don't need to keep it open while traveling
- Send an arrival message upon being within a mile of the destination
- Repeat a previous trip you took
- Preserve battery power by adjusting frequency of location updates

<img src='https://github.com/Thomas-McKanna/Arrived/raw/master/arrived.gif' title='Video Walkthrough' width='300' alt='Video Walkthrough' />

## Notes

The following were used on this project:

- Google Maps API
- Google Places API
- Android Geofencing API
- Android notifications for status of travel
- Foreground service for periodic location updates
- Intent to receive contacts from phone's contact app
- Intent to send SMS messages to contacts
- MVVM architecture
- View binding

## License

    Copyright [2020] [Thomas McKanna]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
