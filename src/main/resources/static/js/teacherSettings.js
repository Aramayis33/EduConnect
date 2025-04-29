document.addEventListener('DOMContentLoaded', () => {

    function showNotification(message, type = 'success') {
        const notification = document.getElementById('notification');
        const notificationMessage = document.getElementById('notification-message');
        const closeBtn = document.getElementById('close-notification');

        notificationMessage.textContent = message;
        notification.classList.remove('hidden', 'success', 'error');
        notification.classList.add(type);

        setTimeout(() => {
            notification.classList.add('hidden');
        }, 5000);

        closeBtn.addEventListener('click', () => {
            notification.classList.add('hidden');
        });
    }

    const editEmailBtn = document.getElementById('edit-email-btn');
    const emailChangeSection = document.getElementById('email-change-section');
    const sendVerificationBtn = document.getElementById('send-verification-btn');
    const verificationSection = document.getElementById('verification-section');
    const verifyEmailBtn = document.getElementById('verify-email-btn');
    const currentEmailElement = document.querySelector('p[th\\:text="${session.teacher.email}"]');

    let countdownTimer;

    if (editEmailBtn && emailChangeSection) {
        editEmailBtn.addEventListener('click', () => {
            emailChangeSection.classList.toggle('hidden');
        });
    }

    if (sendVerificationBtn && verificationSection) {
        sendVerificationBtn.addEventListener('click', () => {
            const newEmail = document.getElementById('new-email').value;
            const currentEmail = currentEmailElement ? currentEmailElement.textContent.trim() : '';

            console.log('Current Email:', currentEmail);
            console.log('New Email:', newEmail);

            if (!newEmail) {
                showNotification('Խնդրում ենք մուտքագրել նոր էլ. փոստ', 'error');
                return;
            }

            if (newEmail === currentEmail) {
                showNotification('Դուք չեք կարող մուտքագրել Ձեր ընթացիկ էլ. փոստը', 'error');
                return;
            }

            sendVerificationBtn.disabled = true;
            startCountdown();

            fetch('/student/confirm-email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({ 'email': newEmail })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.message) {
                        showNotification(data.message, 'success');
                        verificationSection.classList.remove('hidden');
                    } else {
                        showNotification(data.error, 'error');
                        resetButton();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Ինչ-որ սխալ տեղի ունեցավ', 'error');
                    resetButton();
                });
        });
    }

    function startCountdown() {
        const startTime = Date.now();
        localStorage.setItem('countdownStart', startTime);
        localStorage.setItem('countdownDuration', 60);
        let timeLeft = 60;

        sendVerificationBtn.textContent = `Ուղարկել կրկին (${timeLeft} վրկ)`;
        countdownTimer = setInterval(() => {
            timeLeft = Math.max(0, 60 - Math.floor((Date.now() - startTime) / 1000));
            sendVerificationBtn.textContent = `Ուղարկել կրկին (${timeLeft} վրկ)`;
            if (timeLeft <= 0) {
                resetButton();
            }
        }, 1000);
    }

    function resetButton() {
        clearInterval(countdownTimer);
        sendVerificationBtn.disabled = false;
        sendVerificationBtn.textContent = 'Ուղարկել հաստատման կոդ';
        localStorage.removeItem('countdownStart');
        localStorage.removeItem('countdownDuration');
    }

    const startTime = localStorage.getItem('countdownStart');
    const duration = localStorage.getItem('countdownDuration');
    if (startTime && duration) {
        const elapsed = Math.floor((Date.now() - startTime) / 1000);
        let timeLeft = duration - elapsed;

        if (timeLeft > 0) {
            sendVerificationBtn.disabled = true;
            sendVerificationBtn.textContent = `Ուղարկել կրկին (${timeLeft} վրկ)`;
            countdownTimer = setInterval(() => {
                timeLeft = Math.max(0, duration - Math.floor((Date.now() - startTime) / 1000));
                sendVerificationBtn.textContent = `Ուղարկել կրկին (${timeLeft} վրկ)`;
                if (timeLeft <= 0) {
                    resetButton();
                }
            }, 1000);
        } else {
            resetButton();
        }
    }

    if (verifyEmailBtn) {
        verifyEmailBtn.addEventListener('click', () => {
            const code = document.getElementById('verification-code').value;
            if (code) {
                fetch('/student/verify-email', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({ 'code': code })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.message) {
                            showNotification(data.message, 'success');
                            emailChangeSection.classList.add('hidden');
                            verificationSection.classList.add('hidden');
                            location.reload();
                        } else {
                            showNotification(data.error, 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showNotification('Ինչ-որ սխալ տեղի ունեցավ', 'error');
                    });
            } else {
                showNotification('Խնդրում ենք մուտքագրել հաստատման կոդ', 'error');
            }
        });
    }

    const changePasswordBtn = document.getElementById('change-password-btn');
    const passwordChangeSection = document.getElementById('password-change-section');
    const confirmPasswordBtn = document.getElementById('confirm-password-btn');
    const oldPasswordInput = document.getElementById('old-password');
    const newPasswordInput = document.getElementById('new-password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const toggleOldPasswordBtn = document.getElementById('toggle-old-password');
    const toggleNewPasswordBtn = document.getElementById('toggle-new-password');
    const toggleConfirmPasswordBtn = document.getElementById('toggle-confirm-password');

    const reqLength = document.getElementById('req-length');
    const reqUppercase = document.getElementById('req-uppercase');
    const reqLowercase = document.getElementById('req-lowercase');
    const reqNumber = document.getElementById('req-number');
    const reqSymbol = document.getElementById('req-symbol');

    if (changePasswordBtn && passwordChangeSection) {
        changePasswordBtn.addEventListener('click', () => {
            passwordChangeSection.classList.toggle('hidden');
        });
    }

    function validatePassword(password) {
        const lengthValid = password.length >= 8;
        const uppercaseValid = /[A-Z]/.test(password);
        const lowercaseValid = /[a-z]/.test(password);
        const numberValid = /[0-9]/.test(password);
        const symbolValid = /[!@#$%^&*]/.test(password);

        reqLength.classList.toggle('met', lengthValid);
        reqUppercase.classList.toggle('met', uppercaseValid);
        reqLowercase.classList.toggle('met', lowercaseValid);
        reqNumber.classList.toggle('met', numberValid);
        reqSymbol.classList.toggle('met', symbolValid);

        return lengthValid && uppercaseValid && lowercaseValid && numberValid && symbolValid;
    }

    function updateConfirmButton() {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        const oldPassword = oldPasswordInput.value;

        const passwordValid = validatePassword(newPassword);
        const passwordsMatch = newPassword === confirmPassword && newPassword !== '' && oldPassword!==newPassword;

        confirmPasswordBtn.disabled = !(passwordValid && passwordsMatch && oldPassword);
    }

    if (newPasswordInput && confirmPasswordInput && oldPasswordInput) {
        newPasswordInput.addEventListener('input', () => {
            validatePassword(newPasswordInput.value);
            updateConfirmButton();
        });
        confirmPasswordInput.addEventListener('input', updateConfirmButton);
        oldPasswordInput.addEventListener('input', updateConfirmButton);
    }
    if (confirmPasswordBtn) {
        confirmPasswordBtn.addEventListener('click', () => {
            const oldPassword = oldPasswordInput.value;
            const newPassword = newPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            if (newPassword === confirmPassword && oldPassword && validatePassword(newPassword)) {
                fetch('/student/change-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Accept': 'application/json'
                    },
                    body: new URLSearchParams({
                        'oldPassword': oldPassword,
                        'newPassword': newPassword
                    })
                })
                    .then(response => {
                        return response.json().then(data => {
                            return { status: response.status, data };
                        });
                    })
                    .then(({ status, data }) => {
                        if (status === 200 && data.message) {
                            showNotification(data.message, 'success');
                            passwordChangeSection.classList.add('hidden');
                            oldPasswordInput.value = '';
                            newPasswordInput.value = '';
                            confirmPasswordInput.value = '';
                            updateConfirmButton();
                        } else if (data.error) {
                            showNotification(data.error, 'error');
                        } else {
                            showNotification('Անհայտ սխալ', 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Fetch error:', error);
                        showNotification(`Համակարգային սխալ: ${error.message}`, 'error');
                    });
            }
        });
    }

    if (toggleOldPasswordBtn && oldPasswordInput) {
        toggleOldPasswordBtn.addEventListener('click', () => {
            if (oldPasswordInput.type === 'password') {
                oldPasswordInput.type = 'text';
                toggleOldPasswordBtn.textContent = '🙈';
            } else {
                oldPasswordInput.type = 'password';
                toggleOldPasswordBtn.textContent = '👁️';
            }
        });
    }

    if (toggleNewPasswordBtn && newPasswordInput) {
        toggleNewPasswordBtn.addEventListener('click', () => {
            if (newPasswordInput.type === 'password') {
                newPasswordInput.type = 'text';
                toggleNewPasswordBtn.textContent = '🙈';
            } else {
                newPasswordInput.type = 'password';
                toggleNewPasswordBtn.textContent = '👁️';
            }
        });
    }

    if (toggleConfirmPasswordBtn && confirmPasswordInput) {
        toggleConfirmPasswordBtn.addEventListener('click', () => {
            if (confirmPasswordInput.type === 'password') {
                confirmPasswordInput.type = 'text';
                toggleConfirmPasswordBtn.textContent = '🙈';
            } else {
                confirmPasswordInput.type = 'password';
                toggleConfirmPasswordBtn.textContent = '👁️';
            }
        });
    }

    const editImageBtn = document.getElementById('edit-image-btn');
    const imageEditSection = document.getElementById('image-edit-section');
    const profileImageInput = document.getElementById('profile-image');
    const imagePreview = document.getElementById('image-preview');
    const uploadImageBtn = document.getElementById('upload-image-btn');
    const deleteImageBtn = document.getElementById('delete-image-btn');

    if (!editImageBtn || !imageEditSection) {
        console.error('Edit image button or section not found');
    } else {
        editImageBtn.addEventListener('click', () => {
            console.log('Edit image button clicked');
            imageEditSection.classList.toggle('hidden');
        });
    }

    if (profileImageInput && imagePreview) {
        profileImageInput.addEventListener('change', (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    imagePreview.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    if (uploadImageBtn) {
        uploadImageBtn.addEventListener('click', () => {
            const file = profileImageInput.files[0];
            if (file) {
                const formData = new FormData();
                formData.append('profileImage', file);

                fetch('/student/upload-image', {
                    method: 'POST',
                    body: formData
                })
                    .then(response => {
                        return response.json().then(data => {
                            return { status: response.status, data };
                        });
                    })
                    .then(({ status, data }) => {
                        if (status === 200 && data.message) {
                            showNotification(data.message, 'success');
                            imageEditSection.classList.add('hidden');
                            if (data.filePath) {
                                const newImageUrl = `${data.filePath}?t=${new Date().getTime()}`;

                                imagePreview.src = newImageUrl;

                                const mainProfileImage = document.querySelector('#main-profile-image');
                                if (mainProfileImage) {
                                    mainProfileImage.src = newImageUrl;
                                }

                                imagePreview.onerror = () => {
                                    console.error('Image failed to load:', newImageUrl);
                                    showNotification('Նկարը չհաջողվեց բեռնել', 'error');
                                };
                                imagePreview.onload = () => {
                                    console.log('Image loaded successfully:', newImageUrl);
                                };
                            }
                        } else if (data.error) {
                            showNotification(data.error, 'error');
                        } else {
                            showNotification('Անհայտ սխալ', 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showNotification(`Համակարգային սխալ: ${error.message}`, 'error');
                    });
            } else {
                showNotification('Խնդրում ենք ընտրել նկար', 'error');
            }
        });
    }

    if (deleteImageBtn) {
        deleteImageBtn.addEventListener('click', () => {
            fetch('/student/delete-image', {
                method: 'POST'
            })
                .then(response => response.json())
                .then(data => {
                    if (data.message) {
                        imagePreview.src = '/images/default.jpg';
                        showNotification(data.message);
                        imageEditSection.classList.add('hidden');
                    } else {
                        showNotification(data.error || 'Նկարի ջնջումը ձախողվեց');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Ինչ-որ սխալ տեղի ունեցավ');
                });

        });
    }
});