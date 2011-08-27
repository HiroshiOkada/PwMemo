INKSCAPE=/usr/bin/inkscape

SRC=./res/drawable-xhdpi/lock_open.svg \
    ./res/drawable-xhdpi/lock_close.svg \
    ./res/drawable-xhdpi/button_bg.svg \
    ./res/drawable-xhdpi/icon.svg \
    ./res/drawable-ldpi/lock_open.svg \
    ./res/drawable-ldpi/lock_close.svg \
    ./res/drawable-ldpi/button_bg.svg \
    ./res/drawable-ldpi/icon.svg \
    ./res/drawable-hdpi/lock_open.svg \
    ./res/drawable-hdpi/lock_close.svg \
    ./res/drawable-hdpi/button_bg.svg \
    ./res/drawable-hdpi/icon.svg \
    ./res/drawable-mdpi/lock_open.svg \
    ./res/drawable-mdpi/lock_close.svg \
    ./res/drawable-mdpi/button_bg.svg \
    ./res/drawable-mdpi/icon.svg \
    web_hi_res_512.svg

OBJ = $(SRC:%.svg=%.png)

%.png : %.svg
	$(INKSCAPE) --without-gui --file=$< --export-png=$@

all: $(OBJ)

clean: 
	rm $(OBJ)


