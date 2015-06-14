CUR=$(cd $(dirname $0) && pwd)
EXTD=$CUR/ext
LIBD=$CUR/libs

case "$1" in
clean)
	ant clean
	;;
windows32)
	ant windows32
	;;
windows32)
	ant windows64
	;;
linux32)
	ant linux32
	;;
linux64)
	ant linux64
	;;
macosx32)
	ant macosx32
	;;
macosx64)
	ant macosx32
	;;
android)
	ant android
	;;
ios)
	ant ios
	;;
*)
	echo 'specify one of "windows32", "windows64", "linux32", "linux64", "macosx32", "macosx64", "android", "ios" or "clean" as first argument'
	exit 1
	;;
esac