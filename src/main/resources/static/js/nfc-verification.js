document.addEventListener('DOMContentLoaded', function () {
    // Get the elements we need
    const statusMessage = document.getElementById('status-message');
    const verificationForm = document.getElementById('verification-form');
    const verificationProgress = document.getElementById('verification-progress');
    const verifiedInput = document.getElementById('verified');

    // Get username and sessionId from data attributes
    const username = document.getElementById('user-data').dataset.username;
    const sessionId = document.getElementById('session-data').dataset.sessionId;

    window.simulateNfcScan = function () {
        statusMessage.className = 'status waiting';
        verificationProgress.style.display = 'block';

        setTimeout(() => {
            statusMessage.className = 'status success';
            statusMessage.innerHTML = '<p>NFC tag verified successfully!</p>';
            verificationProgress.style.display = 'none';
            verifiedInput.value = 'true';
            verificationForm.style.display = 'block';
        }, 1500);
    }

    function pollVerificationStatus() {
        fetch(`/api/verification/status?sessionId=${sessionId}`)
            .then(response => response.json())
            .then(data => {
                if (data.completed) {
                    statusMessage.className = 'status success';
                    statusMessage.innerHTML = '<p>NFC tag verified successfully!</p>';
                    verificationProgress.style.display = 'none';
                    verifiedInput.value = 'true';
                    verificationForm.style.display = 'block';
                } else {
                    setTimeout(pollVerificationStatus, 2000);
                }
            })
            .catch(error => {
                console.error('Error polling verification status:', error);
                setTimeout(pollVerificationStatus, 5000);
            });
    }

    if (sessionId) {
        pollVerificationStatus();
    }
});