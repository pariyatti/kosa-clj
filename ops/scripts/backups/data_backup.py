import boto3
import requests
import os
import subprocess
import sys
import logging

from discord import Webhook, RequestsWebhookAdapter

log_path = "/var/log/kosa"
file_name = "kosa-backups"
logging.basicConfig(format="%(asctime)s %(funcName)s() [%(levelname)-5.5s]  %(message)s")
file_handler = logging.FileHandler("{0}/{1}.log".format(log_path, file_name))
log_formatter = logging.Formatter("%(asctime)s %(funcName)s() [%(levelname)-5.5s]  %(message)s")
file_handler.setFormatter(log_formatter)
logging.getLogger().addHandler(file_handler)

def is_root():
    return os.geteuid() == 0


def discord_send_message(message):
    """Send a message to pariyatti discord alerts channel

    :param message: text to send to the channel
    :type message: string
    """
    discord_url = os.environ["DISCORD_WEBHOOK_URL"]
    webhook = Webhook.from_url(url=discord_url, adapter=RequestsWebhookAdapter())
    webhook.send(message)


def is_service_running(service_name="kosa-app.service"):
    """Checks if a systemd service is running using shell command systemctl

    :param service_name: name of the service, defaults to "kosa-app.service"
    :type service_name: str, optional
    :return: Returns True if service is running, false otherwise 
    :rtype: bool
    """
    try:
        cmd = ["systemctl", "status", service_name]
        completed = subprocess.run(cmd, capture_output=True, text=True, check=True)
    except subprocess.CalledProcessError as err:
        logging.error("ERROR:", err)
    else:
        for line in completed.stdout.splitlines():
            if "Active:" in line:
                if "(running)" in line:
                    print("{0} service is running".format(service_name))
                    return True
        return False


def stop_service(service_name="kosa-app.service"):
    """Stops a systemd service using shell command systemctl

    :param service_name: name of the service, defaults to "kosa-app.service"
    :type service_name: str, optional
    """
    if not is_root():
        logging.error("This function requires super user privileges")
        sys.exit(1)
    try:
        cmd = ["systemctl", "stop", service_name]
        completed = subprocess.run(cmd, capture_output=True, text=True, check=True)
    except subprocess.CalledProcessError as err:
        logging.error("ERROR:", err)
        discord_send_message(
            "stop_service command failed to run! Please check the logs"
        )


def start_service(service_name="kosa-app.service"):
    """Stops a systemd service using shell command systemctl

    :param service_name: name of the service, defaults to "kosa-app.service"
    :type service_name: str, optional
    """
    if not is_root():
        logging.error("This function requires super user privileges")
        sys.exit(1)
    try:
        cmd = ["systemctl", "start", service_name]
        completed = subprocess.run(cmd, capture_output=True, text=True, check=True)
    except subprocess.CalledProcessError as err:
        logging.error("ERROR:", err)
        discord_send_message(
            "Kosa backend down! start_service command failed to run, please check the logs"
        )

