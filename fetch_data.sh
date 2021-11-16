GET "https://umjicanvas.com/api/v1/users/self/favorites/courses?access_token=$1" > /tmp/temp.txt
courses=($(python ./canvas.py xxx -f | tr -d '[],'))
rm /tmp/temp.txt
len_course=${#courses[@]}
for (( i = 1; i < ${len_course}; i += 2 ));
do
	for j in {1..5}
	do
		course_id=${courses[$i]}
		course_name=${courses[$(($i + 1))]}
		var=`GET "https://umjicanvas.com/api/v1/courses/$course_id/users?access_token=$1&include[]=email&include[]=enrollments&per_page=50&page=$j"`
		len=`echo -n $var | wc -m`
		if [ $len -gt 100 ]; then
			echo $var >> /tmp/$course_name.txt
		else
			break
		fi
	done
	python ./canvas.py $course_name
	echo "Fetching data from $course_name"
	rm /tmp/$course_name.txt
done
my_activity=0
for (( i = 1; i < ${len_course}; i += 2 ));
do
	course_name=${courses[$(($i + 1))]}
	delta=`python ./canvas.py $course_name -m ${courses[0]}`
	my_activity=$((my_activity + $delta))
	python ./canvas.py $course_name -r
done
python ./canvas.py xxx -s $my_activity