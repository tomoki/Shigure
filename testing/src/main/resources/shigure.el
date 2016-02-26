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

(defface shigure-evaluated-face
  '((t :underline (:color "green1")))
  "the animations which was executed")

(defun shigure-put-evaluated (beg end)
  (shigure-put-face 'shigure-evaluated-face beg end))

(defun shigure-remove-all-overlay ()
  (remove-overlays (point-min) (point-max) 'category 'shigure-face))

(defun put-evaluated (lines)
  (shigure-remove-all-overlay)
  (save-excursion
    (dolist (elt lines)
      (goto-line (+ 1 elt))
      (shigure-put-evaluated (line-beginning-position)
                             (line-end-position)))))

(defun start-shigure (n)
  "client for shigure"
  (interactive "nPort: ")
  (set (make-local-variable 'epc)
       (epc:start-epc-debug n))
  (set (make-local-variable 'last-post-command-position)
       0)
  (add-to-list 'post-command-hook #'after-move)
  (add-hook 'after-change-functions (lambda (a b c) (evaluate-me)) t t)
  )

