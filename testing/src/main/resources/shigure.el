(require 'epc)

(defun copy-to-somewhere ()
  (let ((temp (make-temp-file "")))
    (write-region (point-min) (point-max) temp)
    temp))

(defun evaluate-me ()
  (interactive)
  (deferred:$
    (epc:call-deferred epc 'evalfile (list (copy-to-somewhere)))
    (deferred:nextc it
      (lambda (x)
        (progn
          (moveline)
          (message "%S" x))))))

(defun calc-current-line ()
  (save-restriction
    (widen)
    (save-excursion
      (beginning-of-line)
      (count-lines 1 (point)))))

(defun moveline ()
  (interactive)
  (deferred:$
    (epc:call-deferred epc 'moveline (list (calc-current-line)))
    (deferred:nextc it
      (lambda (x)
        (progn
          (put-evaluated x)
          (message "Actives : %S" x))))))


(defun after-move ()
  (unless (equal (point) last-post-command-position)
    (progn
      (moveline)
      (setq last-post-command-position (point)))))

(defun shigure-put-face (face beg end)
  (save-restriction
    (let ((ol (make-overlay beg end)))
      (progn
        (overlay-put ol 'category 'shigure-face)
        (overlay-put ol 'face face)
        ol))))

(defface shigure-evaluated-accepted-face
  '((t :underline (:color "green1")))
  "the animations which was executed")

(defface shigure-evaluated-normal-face
  '((t :underline (:color "blue1")))
  "the animations which was executed")

(defface shigure-evaluated-declined-face
  '((t :underline (:color "red1")))
  "the animations which was executed")

(defun shigure-put-evaluated-normal (beg end)
  (shigure-put-face 'shigure-evaluated-normal-face beg end))

(defun shigure-put-evaluated-accepted (beg end)
  (shigure-put-face 'shigure-evaluated-accepted-face beg end))

(defun shigure-put-evaluated-declined (beg end)
  (shigure-put-face 'shigure-evaluated-declined-face beg end))

(defun shigure-remove-all-overlay ()
  (remove-overlays (point-min) (point-max) 'category 'shigure-face))

(defun put-evaluated (lines)
  (shigure-remove-all-overlay)
  (save-excursion
    (dolist (elt lines)
      (goto-line (+ 1 (car elt)))
      (cond
       ((= (cadr elt) 0)
        (shigure-put-evaluated-declined (line-beginning-position)
                                        (line-end-position)))
       ((= (cadr elt) 1)
        (shigure-put-evaluated-normal (line-beginning-position)
                                      (line-end-position)))
       ((= (cadr elt) 2)
        (shigure-put-evaluated-accepted (line-beginning-position)
                                        (line-end-position)))
       ))))

(defun shigure-accept ()
  (interactive)
  (save-excursion
    (back-to-indentation)
    (insert "✔")))

(defun shigure-decline ()
  (interactive)
  (save-excursion
    (back-to-indentation)
    (insert "✘")))

(defun start-shigure (n)
  "client for shigure"
  (interactive "nPort: ")
  (set (make-local-variable 'epc)
       (epc:start-epc-debug n))
  (evaluate-me)
  (set (make-local-variable 'last-post-command-position)
       0)
  (define-key (current-local-map) "\C-xa" 'shigure-accept)
  (define-key (current-local-map) "\C-xd" 'shigure-decline)
  (add-to-list 'post-command-hook #'after-move)
  (add-hook 'after-change-functions (lambda (a b c) (evaluate-me)) t t)
  )

