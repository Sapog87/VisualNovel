while getopts s:k: flag
do
    case "${flag}" in
        s) remote_server=${OPTARG};;
        k) ssh_key=${OPTARG};;
    esac
done

programs=("docker" "docker-compose")

ssh $remote_server -i $ssh_key "rm -r vn; mkdir vn"

tar -cvzf vn.tar --exclude='.git' .

scp -i $ssh_key vn.tar $remote_server:~/vn/vn.tar

ssh $remote_server -i $ssh_key "cd vn; tar -xvzf vn.tar"

rm -r vn.tar

for program_name in "${programs[@]}"; do
    if ! ssh $remote_server -i $ssh_key "command -v $program_name" &> /dev/null; then
        echo "$program_name is not installed."
        echo "Installing $package_name..."
        ssh $remote_server -i $ssh_key "apt install $package_name -y"
    else
        echo "$program_name is already installed."
    fi
done

ssh $remote_server -i $ssh_key "cd vn; docker-compose down; docker-compose up --build -d"