#!/bin/bash

cd ./res/drawable-xhdpi
ruby ../../add9patch.rb button_bg.png  63 47 2 2 12 12 104 72         

cd ../../res/drawable-hdpi
ruby ../../add9patch.rb button_bg.png   47 35 2 2  9  9  78 54    

cd ../../res/drawable-mdpi
ruby ../../add9patch.rb button_bg.png   31 23 2 2  6  6  52 36   

cd ../../res/drawable-ldpi
ruby ../../add9patch.rb button_bg.png   23 17 2 2  4  4  40 28  

