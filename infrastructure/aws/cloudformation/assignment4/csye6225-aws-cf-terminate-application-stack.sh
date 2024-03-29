#!/bin/bash

Stack_list=$(aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE --query 'StackSummaries[].StackName' --output text )
echo Stack list: $Stack_list
#delete function for deleting stacks

delete(){
    read stack

    id=$(aws cloudformation describe-stacks --stack-name $stack --query "Stacks[*].StackId" --output text 2>&1)

    aws cloudformation delete-stack --stack-name $stack

    aws cloudformation wait stack-delete-complete --stack-name $stack
    aws cloudformation describe-stacks --stack-name $id --query "Stacks[*].StackStatus" --output text
}
echo Alert! Please select the EC2 instance stack first.
delete
echo Now select other stack
delete
echo All stacks deleted successfully! Now deleting EBS volumes.
#Deleting volumes

aws ec2 describe-volumes --query 'Volumes[*].{ID:VolumeId}' --output text > vol.txt
len=`wc -l <vol.txt`
i=1
while [ $i -le $len ];
do
	iD=$(awk NR=="$i" vol.txt)
	#echo "$iD"
	aws ec2 delete-volume --volume-id "$iD"
  	i=$(($i + 1))
done

echo Volumes deleted successfully!

exit 0
