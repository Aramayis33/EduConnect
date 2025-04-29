$(document).ready(function () {
    function showStep(stepId) {
        $('.step').removeClass('active');
        $(`#${stepId}`).addClass('active');
    }

    function showNotification(message, type) {
        const errorElements = {
            'step-email': '#email-error',
            'step-code': '#code-error',
            'step-reset': '#password-error'
        };
        const currentStep = $('.step.active').attr('id');
        const errorElement = errorElements[currentStep];
        $(errorElement).text(message).css('color', type === 'error' ? '#e53935' : '#00bcd4');
        setTimeout(() => $(errorElement).text(''), 5000);
    }

    window.sendEmail = function () {
        const email = $('#email').val().trim();
        if (!email) {
            showNotification('Խնդրում ենք մուտքագրել էլ․ հասցե', 'error');
            return;
        }

        $.ajax({
            url: '/email-confirm',
            method: 'POST',
            data: { email: email },
            success: function (response) {
                showNotification(response.message, 'success');
                showStep('step-code');
            },
            error: function (xhr) {
                const errorMsg = xhr.responseJSON?.error || 'Ինչ-որ բան սխալ է, խնդրում ենք կրկին փորձել';
                showNotification(errorMsg, 'error');
            }
        });
    };

    window.verifyCode = function () {
        const code = $('#code').val().trim();
        if (!code) {
            showNotification('Խնդրում ենք մուտքագրել կոդը', 'error');
            return;
        }

        $.ajax({
            url: '/check-code',
            method: 'POST',
            data: { code: code },
            success: function (response) {
                showNotification('Կոդը հաստատված է', 'success');
                showStep('step-reset');
            },
            error: function (xhr) {
                const errorMsg = xhr.responseJSON?.error || 'Կոդը սխալ է';
                showNotification(errorMsg, 'error');
            }
        });
    };

    $('.toggle-password').on('click', function () {
        const $input = $(this).prev('.input-field');
        const isPassword = $input.attr('type') === 'password';
        $input.attr('type', isPassword ? 'text' : 'password');
        $(this).text(isPassword ? '🙈' : '👁️');
    });

    function updatePasswordRequirements() {
        const password = $('#password').val();
        const confirmPassword = $('#confirm-password').val();
        const $resetButton = $('#reset-button');

        const requirements = {
            'req-length': password.length >= 8,
            'req-uppercase': /[A-Z]/.test(password),
            'req-lowercase': /[a-z]/.test(password),
            'req-number': /\d/.test(password),
            'req-symbol': /[!@#$%^&*(),.?":{}|<>]/.test(password),
            'req-match': password && password === confirmPassword
        };

        let allMet = true;
        for (const [id, met] of Object.entries(requirements)) {
            const $li = $(`#${id}`);
            $li.find('.icon').text(met ? '✓' : '✗').removeClass('red green').addClass(met ? 'green' : 'red');
            if (!met) allMet = false;
        }

        $resetButton.prop('disabled', !allMet).css('opacity', allMet ? '1' : '0.5');
    }

    $('#password, #confirm-password').on('input', updatePasswordRequirements);

    window.resetPassword = function () {
        console.log("resetPassword function called");
        const password = $('#password').val();
        const confirmPassword = $('#confirm-password').val();

        if (password !== confirmPassword) {
            showNotification('Գաղտնաբառերը չեն համընկնում', 'error');
            return;
        }

        console.log("Sending AJAX request to /new-password with password:", password);
        $.ajax({
            url: '/new-password-activate',
            method: 'POST',
            data: { password: password },
            success: function (response) {
                console.log("Success response:", response);
                if (response && response.message) {
                    showNotification(response.message, 'success');
                    setTimeout(() => window.location.href = '/', 1500);
                } else if (typeof response === 'string') {
                    showNotification(response, 'success');
                    setTimeout(() => window.location.href = '/', 1500);
                }
            },
            error: function (xhr) {
                console.error("Error response:", xhr);
                const errorMsg = xhr.responseJSON?.error || 'Ինչ-որ բան սխալ է, խնդրում ենք կրկին փորձել';
                showNotification(errorMsg, 'error');
            }
        });
    };
});