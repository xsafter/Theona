# Import necessary module
import fileinput
import re

# List of dependencies to remove
deps_to_remove = []
with open('unused_dependencies.txt', 'r') as f:
    for line in f:
        if re.search(r'- .*\(.*\)', line):
            dep = line.split('- ')[1].strip()
            deps_to_remove.append(dep)

# Go through build file and check each line
with fileinput.FileInput('app/build.gradle.kts', inplace=True) as file:
    for line in file:
        if not any(dep in line for dep in deps_to_remove):
            print(line, end='')
