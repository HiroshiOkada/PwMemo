#!/usr/bin/env ruby
#
require 'rubygems'
require 'RMagick'
include Magick

def add9patch( src_filename, expand_x, expand_y, expand_width, expand_height, content_x, content_y, content_width, content_height)
	dst_filename = src_filename.gsub( /\.png$/, '.9.png')
	img = Image.read("button_bg.png")[0]
	img.border!( 1, 1, "transparent")

	(1..expand_width).each do | x |
		img.pixel_color( expand_x+x, 0, "#000F")
	end
	(1..expand_height).each do | y |
		img.pixel_color( 0, expand_y+y, "#000F")
	end
	ymax = img.rows - 1
	(1..content_width).each do | x |
		img.pixel_color( content_x+x, ymax, "#000F")
	end
	xmax = img.columns - 1
	(1..content_height).each do | y |
		img.pixel_color( xmax, content_y+y, "#000F")
	end
	img.write( dst_filename)
end


if __FILE__ == $0
	if ARGV.size != 9 
		puts "$0 src_filename expand_x expand_y expand_width expand_height content_x content_y content_width content_height"
	else
		add9patch ARGV[0], *ARGV[1,8].map(&:to_i)
	end
end

