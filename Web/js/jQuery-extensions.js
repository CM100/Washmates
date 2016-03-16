jQuery.fn.extend({
    showConfirmDialog: function (confirmMessage, okLabel, cancelLabel) {
        okLabel = okLabel || 'Ok';
        cancelLabel = cancelLabel || 'Cancel';
        var html = '<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="confirm-modal-title" aria-hidden="true">' +
            '<div class="modal-dialog">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            //                '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '<h2 class="modal-title" id="confirm-modal-title" style="color:#8d9095;"><i class="fa fa-exclamation-circle"></i> Confirm action</h2>' +
            '</div>' +
            '<div class="modal-body" style="color:#4A4A4A; text-align:center;">' +
            confirmMessage +
            '</div>' +
            '<div class="modal-footer" style="text-align:center;">' +
            '<button id="confirm-ok-button" type="button" class="btn btn-primary">' + okLabel + '</button>' +
            '<button id="confirm-cancel-button" type="button" class="btn">' + cancelLabel + '</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';

        var $modal = jQuery(html);
        $modal.css('font-weight', 'bold');

        $modal.appendTo(this);
        $modal.modal({
            show: true,
            keyboard: 'false',
            backdrop: 'static'
        });
        $modal.on('click', '#confirm-ok-button', function () {
            $modal.trigger('ok');
            cleanup();
        });
        $modal.on('click', '#confirm-cancel-button, button.close', function () {
            $modal.trigger('cancel');
            cleanup();
        });

        var cleanup = function () {
            $modal.modal('hide');
            var timeoutid = window.setTimeout(function () {
                $modal.off();
                $modal.remove();
                window.clearTimeout(timeoutid);
            }, 500);
        };

        return $modal;
    },
    showAlertDialog: function (confirmMessage, okLabel) {
        okLabel = okLabel || 'Ok';
        var html = '<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="confirm-modal-title" aria-hidden="true">' +
            '<div class="modal-dialog">' +
            '<div class="modal-content">' +
            '<div class="modal-header btn-primary">' +
            //                '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '<h2 class="modal-title" id="confirm-modal-title"><i class="fa fa-exclamation-circle"></i> Info</h2>' +
            '</div>' +
            '<div class="modal-body" style="color:#4A4A4A; text-align:center;">' +
            (confirmMessage.error || confirmMessage.message || confirmMessage) +
            '</div>' +
            '<div class="modal-footer" style="text-align:center;">' +
            '<button id="confirm-ok-button" type="button" class="btn btn-primary">' + okLabel + '</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';

        var $modal = jQuery(html);
        $modal.css('font-weight', 'bold');

        $modal.appendTo(this);
        $modal.modal({
            show: true,
            keyboard: 'false',
            backdrop: 'static'
        });
        $modal.on('click', '#confirm-ok-button', function () {
            $modal.trigger('ok');
            cleanup();
        });

        var cleanup = function () {
            $modal.modal('hide');
            var timeoutid = window.setTimeout(function () {
                $modal.off();
                $modal.remove();
                window.clearTimeout(timeoutid);
            }, 500);
        };

        return $modal;
    }
});
