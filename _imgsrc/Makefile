INKSCAPE=/usr/bin/inkscape

RESPNG=./res/drawable-xhdpi/lock_open.png \
       ./res/drawable-xhdpi/lock_close.png \
       ./res/drawable-xhdpi/icon.png \
       ./res/drawable-ldpi/lock_open.png \
       ./res/drawable-ldpi/lock_close.png \
       ./res/drawable-ldpi/icon.png \
       ./res/drawable-hdpi/lock_open.png \
       ./res/drawable-hdpi/lock_close.png \
       ./res/drawable-hdpi/icon.png \
       ./res/drawable-mdpi/lock_open.png \
       ./res/drawable-mdpi/lock_close.png \
       ./res/drawable-mdpi/icon.png

WEBPNG=./web_hi_res_512.png

RESNINE=./res/drawable-xhdpi/button_bg.9.png \
        ./res/drawable-ldpi/button_bg.9.png \
	./res/drawable-hdpi/button_bg.9.png \
        ./res/drawable-mdpi/button_bg.9.png 

PNG = $(RESPNG) $(WEBPNG) $(RESNINE:%.9.png=%.png)

%.png : %.svg
	$(INKSCAPE) --without-gui --file=$< --export-png=$@

all: $(RESPNG) $(RESNINE) $(WEBPNG)

install: $(RESPNG) $(RESNINE)
	cp --parents $^ ../

${RESNINE} : $(RESNINE:%.9.png=%.png)
	sh make9patch.sh

clean: 
	rm $(PNG) $(RESNINE)


