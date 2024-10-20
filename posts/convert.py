import os
import re
import sys


def sanitize_filename(title):
    # Replace any characters that are not valid in UNIX file names
    title = re.sub(
        r"[^\w\s]", "", title
    )  # Remove all non-alphanumeric characters except spaces
    return title.strip()  # Strip leading/trailing spaces


def convert_image_tags(content):
    # Regular expression to match markdown images wrapped in links
    linked_image_pattern = re.compile(
        r"\[!\[.*?\]\((.*?)\)\]\(.*?\)(,width=\d+,height=\d+\])?"
    )
    # Regular expression to match standalone markdown images
    image_tag_pattern = re.compile(r"!\[.*?\]\((.*?)\)(,width=\d+,height=\d+\])?")

    def replace_linked_image(match):
        # Extract the image URL and replace with Obsidian image embed syntax
        image_url = match.group(1)
        if not image_url.startswith("http"):
            # Internal image
            return f"![[{image_url}]]"
        return f"![{match.group(1)}]({image_url})"  # External image

    def replace_image_tag(match):
        image_url = match.group(1)
        if image_url.startswith("http"):
            # External link
            return f"![{match.group(1)}]({image_url})"
        else:
            # Internal link (Obsidian style)
            return f"![[{image_url}]]"

    # Replace linked images first, then regular images
    content = linked_image_pattern.sub(replace_linked_image, content)
    return image_tag_pattern.sub(replace_image_tag, content)


def process_markdown(file_path):
    # Read the file
    with open(file_path, "r") as file:
        lines = file.readlines()

    # Find the first line that starts with a headline
    title_line = None
    content_lines = []

    for line in lines:
        if title_line is None and line.startswith("# "):  # Headline in markdown
            title_line = line.strip("# ").strip()  # Extract title
            continue  # Skip the title line from the content
        content_lines.append(line)  # Keep other content

    if title_line is None:
        print("No title found in the markdown file.")
        return

    # Sanitize the title to create a valid filename
    sanitized_title = sanitize_filename(title_line)

    # Prepare the new filename
    new_filename = f"{sanitized_title}.md"

    # Join content lines and convert image tags
    content = "".join(content_lines)
    content = convert_image_tags(content)

    # Write the new content without the title to the new file
    with open(new_filename, "w") as new_file:
        new_file.write(content)

    # Optionally, delete the original file (uncomment to enable)
    os.remove(file_path)

    print(f"File renamed to: {new_filename}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python convert_md_file.py <filename>")
        sys.exit(1)

    input_file = sys.argv[1]

    if not os.path.isfile(input_file):
        print(f"File not found: {input_file}")
        sys.exit(1)

    process_markdown(input_file)
