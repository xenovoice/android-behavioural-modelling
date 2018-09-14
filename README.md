# BEHAVIOUR MODELING ON MOBILE DEVICES AND WEARABLES

Recent years have seen a phenomenal global widespread use of smart mobile devices that are equipped with various embedded sensors, which makes it possible to capture the physical and virtual context of the user and the surroundings. It is important to accurately model human behavior based on sensor data to enable context aware decision making.

Many behavioral metrics algorithms ingest multiple data from various sensors and correlate them to build behavioral models that are capable of performing quantitative analysis on each unique individuals. This opens up a new means of performing continuous authentication on the original creator of the sensor data. The objective of this project is to perform behavioral modeling of sensor data from mobile device and
wearables through feature engineering. Subsequently, the student can leverage on several machine learning techniques to perform correlation and structural activity recognition of a unique individual.

This project consists of an Android app named UProfile, both for an Android mobile device (tested on a Samsung Galaxy Note 5) and an Android Wear (tested on a Moto360 first gen).

This is an Integrative Team Project done by a group of students from Singapore Institute of Technology.

## Pre-requisites
- Android Studio
- Python 3
- Tensorflow (Version 1.1.0)

## UProfile (Android mobile device)
<img src="https://user-images.githubusercontent.com/26968011/44968250-6aa67e80-af79-11e8-9e3e-deaa6429ed91.png" width="280" height="480"> <img src="https://user-images.githubusercontent.com/26968011/44968268-9590d280-af79-11e8-81a4-dc0c280e5bab.png" width="280" height="480"> <img src="https://user-images.githubusercontent.com/26968011/44968274-9c1f4a00-af79-11e8-99f3-db6764dfc4b2.png" width="280" height="480"> 
<img src="https://user-images.githubusercontent.com/26968011/44968279-a0e3fe00-af79-11e8-82b5-1c9ee49659ed.png" width="280" height="480"> <img src="https://user-images.githubusercontent.com/26968011/44968280-a5101b80-af79-11e8-9e69-dc91d413bb7a.png" width="280" height="480"> <img src="https://user-images.githubusercontent.com/26968011/44968737-2bc5f800-af7c-11e8-8fcc-8a06d843aad5.png" width="280" height="480">
<img src="https://user-images.githubusercontent.com/26968011/44968792-75164780-af7c-11e8-95a5-b3f03db28995.png" width="280" height="480"> <img src="https://user-images.githubusercontent.com/26968011/44968794-75aede00-af7c-11e8-8292-bcdb7dbc1a6a.png" width="280" height="480"> <img src="https://user-images.githubusercontent.com/26968011/44969175-7fd1dc00-af7e-11e8-8468-2e3086c3e784.png" width="280" height="480">

## UProfile (Android Wear)
This wearable app detects what activity the user is performing.

<img src="https://user-images.githubusercontent.com/26968011/44969038-d985d680-af7d-11e8-8ab2-5847969c11fb.jpg" width="210" height="182"> <img src="https://user-images.githubusercontent.com/26968011/44969039-d985d680-af7d-11e8-9690-6985874b8466.jpg" width="210" height="182"> <img src="https://user-images.githubusercontent.com/26968011/44969040-da1e6d00-af7d-11e8-9c61-fb63e7d08d6d.jpg" width="210" height="182"> <img src="https://user-images.githubusercontent.com/26968011/44969041-da1e6d00-af7d-11e8-91eb-468d8e8a911d.jpg" width="210" height="182">

## Instructions:

### Trainer (LSTM)
1. Place the "docker_lstm_trainer.py" preferably in a docker image with Tensorflow version 1.1.0 installed
2. Place the collected dataset in the same directory as the "docker_lstm_trainer.py" script (E.g. wear_dataset.txt)
3. To run:  ./docker_lstm_trainer.py wear_dataset.txt
