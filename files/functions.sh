INDENT="%1s"
GOOD=$(printf '\033[32;01m')
WARN=$(printf '\033[33;01m')
BAD=$(printf '\033[31;01m')
HILITE=$(printf '\033[36;01m')
BRACKET=$(printf '\033[34;01m')
NORMAL=$(printf '\033[0m')

ask()
{
  while true; do
    read -p "$1? (y/n) " yn
    case $yn in
      [Yy]* ) return 0; break;;
      [Nn]* ) return 1; break;;
      * ) break ;;
    esac
  done
}

echoc ()
{
  echo -e "\E["$1"m$2\E[0m";
}

newline ()
{
  echo -e "\n"
}

einfo ()
{
  printf "${GOOD}*${NORMAL}${INDENT}$*\n"
}

ewarn ()
{
  printf "${WARN}*${NORMAL}${INDENT}$*\n" >&2
}

eerror ()
{
  printf "${BAD}*${NORMAL}${INDENT}$*\n" >&2
}

check_root()
{
  if [ $EUID -ne 0 ]; then
      eerror "Please run as root.";
      exit $?
  fi
}

run_as_root()
{
  if [ $EUID -ne 0 ]; then
    if [ -t 1 ]; then
      sudo "$0" "$@"
    fi
    exit
  fi
}

export echoc
