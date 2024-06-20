while getopts s:k:t:v:d: flag
do
    case "${flag}" in
        s) remote_server=${OPTARG};;
        k) ssh_key=${OPTARG};;
    esac
done

programs=("docker" "docker-compose")

ssh $remote_server -i $ssh_key "rm -r vn; mkdir vn"

tar cvf vn.tar .

scp -i $ssh_key vn.tar $remote_server:~/vn/vn.tar

ssh $remote_server -i $ssh_key "cd vn; tar -xvf vn.tar"

rm -r vn.tar

for program_name in "${programs[@]}"; do
    if ! ssh $remote_server -i $ssh_key "command -v $program_name" &> /dev/null; then
        echo "$program_name is not installed."
        package_name=$(apt-cache search $program_name | grep -E "^$program_name" | awk '{print $1}')
        if [ -n "$package_name" ]; then
            echo "Installing $package_name..."
            ssh $remote_server -i $ssh_key "apt install $package_name -y"
        else
            echo "Package $program_name not found."
            exit 1
        fi
    else
        echo "$program_name is already installed."
    fi
done

ssh $remote_server -i $ssh_key "cd vn; docker-compose down; docker-compose up --build -d"