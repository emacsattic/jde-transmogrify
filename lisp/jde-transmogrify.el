;;; jde-parse.el
;; $Revision: 0.01 $ 

;; Author: Nascif A. Abousalh Neto <nascif@acm.org>
;; Maintainer: Nascif A. Abousalh Neto
;; Keywords: java, refactoring, tools

;; Copyright (C) 1997, 1998, 2000, 2001 Nascif A. Abousalh Neto

;; GNU Emacs is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; GNU Emacs is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to the
;; Free Software Foundation, Inc., 59 Temple Place - Suite 330,
;; Boston, MA 02111-1307, USA.

; Note. These commands use an external Java library implemented in Java to apply
; refactorings to Java source files. These commands use the JDE's integrated
; Java source interpreter, the BeanShell, to invoke the refactoring library
; Transmogrify (http://transmogrify.sourceforge.net/). If the BeanShell is not
; running, these commands will start it. Thus, the first time you invoke a
; refactoring command you may notice a slight delay before getting a
; response. Thereafter, the response should be very fast.

; {{{ Dependencies

(require 'jde)

; }}}

; {{{ Customization

; (defgroup jde-transmogrify nil
;   "JDE interface to Transmogrify Java Refactoring library"
;   :group 'jde
;   :prefix "jde-transmogrify-")

; (defcustom jde-transmogrify-install-path nil
;   "Path to Transmogrify installation."
;   :group 'jde-transmogrify
;   :type 'file)

(defvar jde-transmogrify-install-path "d:/bin/Transmogrify-M2/")

(defvar jde-transmogrify-dependencies '("extensions/antlr.jar" 
                                        "extensions/antlr-full.jar" 
                                        "lib/transmogrify.jar"))

(defvar jde-transmogrify-jar "d:/dev/jde-transmogrify/lib/jde-transmogrify.jar")

(defvar jde-transmogrify-lint-class-names '("net.sourceforge.transmogrify.lint.UnbracedBlockLint"
                                            "net.sourceforge.transmogrify.lint.MultipleReturnLint"
                                            "net.sourceforge.transmogrify.lint.EmptyReturnLint"
                                            "net.sourceforge.transmogrify.lint.UnusedVariableLint"
                                            "net.sourceforge.transmogrify.lint.UnusedParameterLint"))

;(defvar jde-transmogrify-sources '("d:/dev/jde-transmogrify/java/src/"))

; }}}


;;;{{{ Public API

;; Nothing happens; should we create an output buffer (in compile mode, like lint) here 
;; and have the lisp hooks write the references to it?
(defun jde-transmogrify-show-references ()
  (interactive)
  (jde-transmogrify-display "*Transmogrify Show References*" "" t)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.ShowReferencesRefactorer")
)

; OK
(defun jde-transmogrify-find-definition ()
  (interactive)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.FindDefinitionRefactorer")
)

; OK
(defun jde-transmogrify-rename-symbol ()
 "renames the symbol at the caret -- works for methods and vars.  
  Asks the user for the new name of the symbol and renames the symbol based
  on scope."
  (interactive)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.RenameVariable")
)

(defun jde-transmogrify-inline-temp ()
  "implements \"inline temp\" [Fowler, 119]"
  (interactive)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.InlineTemp")
)

; Kinda works; don't take local variables into account.
; Caret must point to the variable definition.
(defun jde-transmogrify-replace-temp-with-query ()
  "implements \"replace temp with query\" [Fowler, 120]"
  (interactive)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.ReplaceTempWithQuery")
)

; OK
; creates only void methods
(defun jde-transmogrify-extract-method ()
  (interactive)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.ExtractMethod")
)

; broken: not adding variables to superclass
(defun jde-transmogrify-pull-up-field-refactorer ()
  (interactive)
  (jde-transmogrify-call "net.sourceforge.transmogrify.refactorer.PullUpFieldRefactorer")
)

; broken for if-then-else
(defun jde-transmogrify-block-bracer ()
  (interactive)
  (if mark-active 
      (jde-transmogrify-call "net.sourceforge.transmogrify.twiddle.BlockBracer")
    (message "Please mark region before using this command"))
)

; broken: Transmogrify internal exception  
(defun jde-transmogrify-try-catch-encloser ()
  (interactive)
  (if mark-active 
      (jde-transmogrify-call "net.sourceforge.transmogrify.twiddle.TryCatchEncloser")
    (message "Please mark region before using this command"))
)

;OK, with some false warnings for inner class variables (closed on CVS, must test)
(defun jde-transmogrify-lint ()
  "Runs Transmogrify lint on selected files"
  (interactive)
  (when (jde-transmogrify-init-check)
    (save-some-buffers (not compilation-ask-about-save) nil)
    (let ((lint-output
           (jde-jeval (concat "jde.transmogrify.EmacsTransmogrifier.lint();"))))
      (if lint-output
          (jde-transmogrify-display "*Transmogrify Lint*" lint-output t)
        (message "No lint warnings" (buffer-name)))))
)
;;;}}}

;;;{{{ Utilities
(defun jde-transmogrify-call (transmogrifier-class-name)
  (when (jde-transmogrify-init-check)
    (let ((transmogrify-error
           (jde-jeval (concat "jde.transmogrify.EmacsTransmogrifier.transmogrify(\""
                              transmogrifier-class-name "\");"))))
      (if transmogrify-error
          (jde-transmogrify-display 
           "*Transmogrify Error*" 
           (concat transmogrifier-class-name ":\n" transmogrify-error) nil)
        (message "Transmogrify successful"))))
)

(defun jde-transmogrify-init-check ()
  "Load the Transmogrify libraries and parse files, if necessary"
  (message "jde-transmogrify-init-check")
  (when (not (jde-jeval-r "jde.util.JdeUtilities.classExists(\"jde.transmogrify.EmacsTransmogrifier\");"))
    (dolist (jar jde-transmogrify-dependencies nil)
      (jde-jeval (concat "addClassPath(\"" (expand-file-name jar jde-transmogrify-install-path) "\");")))
    (jde-jeval (concat "addClassPath(\"" jde-transmogrify-jar "\");")))
  (if (jde-jeval-r "jde.transmogrify.EmacsTransmogrifier.checkInit();")
      (jde-transmogrify-load-source)
    (message "Error initializing Java Transmogrify interface") 
    nil)
)

(defun jde-transmogrify-load-source ()
  (message "jde-transmogrify-load-source")
  (let ((src-list 
;         (or jde-transmogrify-sources 
             jde-db-source-directories
;             )
         ))
    (if (null src-list)
      (message "No source files to parse. Please define jde-transmogrify-sources or jde-db-source-directories")
    (let ((src-ar (jde-transmogrify-list-to-string-array src-list)))
      (message (concat "Loading files " src-ar))
      ;; can't return a Lisp boolean from parseSource because Transmogrify writes the parse times to stdout
      (jde-jeval (concat "jde.transmogrify.EmacsTransmogrifier.parseSource(" src-ar ");"))
      (jde-jeval-r "jde.transmogrify.EmacsTransmogrifier.checkParsing();"))))
)

(defun jde-transmogrify-list-to-string-array (str-list)
  (concat "new String[]{\""
          (car str-list)
          (jde-transmogrify-list-to-string-array-aux (cdr str-list))
          "\"}")
)

(defun jde-transmogrify-list-to-string-array-aux (str-list)
  (if (null str-list) 
      ""
    (concat "\", \"" 
            (car str-list) 
            (jde-transmogrify-list-to-string-array-aux (cdr str-list))))
)

(defun jde-transmogrify-display (title text compile)
  (let* ((parser-buffer-name title)
	 (buf (get-buffer parser-buffer-name))) 
    (if (not buf)
	(setq buf (get-buffer-create parser-buffer-name)))
    (set-buffer buf)
    (erase-buffer)
    (insert text)
    (pop-to-buffer buf)
    (when compile (compilation-mode)))
)
;;;}}}



