import os
import re
import sys


def sanitize_filename(title):
    # Remove only / \ : from the filename
    title = re.sub(r"[\/\\:]", "", title)
    return title.strip()


def convert_image_tags(content):
    # Regular expression to match markdown images wrapped in links with extra brackets or trailing characters
    linked_image_pattern = re.compile(r"\[!\[\[([^\]]+)\]\]\([^\)]+\)]?")
    # Regular expression to match standalone markdown images with trailing characters like ",width=\d+,height=\d+\]"
    image_tag_pattern = re.compile(r"!\[.*?\]\((.*?)\)(,width=\d+,height=\d+\])?")

    def replace_linked_image(match):
        # Extract the image URL and replace with Obsidian image embed syntax
        image_url = match.group(1)
        return f"![[{image_url}]]"

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


def process_markdown(file_content, original_filename):
    lines = file_content.splitlines()

    # Find the first line that starts with a headline
    title_line = None
    frontmatter_block = []
    content_lines = []
    in_frontmatter = False

    for line in lines:
        if line.strip() == "---" and not in_frontmatter:
            in_frontmatter = True
            frontmatter_block.append(line)
            continue
        elif line.strip() == "---" and in_frontmatter:
            frontmatter_block.append(line)
            content_lines.append(line)  # Keep the closing frontmatter marker
            in_frontmatter = False
            continue

        if in_frontmatter:
            frontmatter_block.append(line)
        elif title_line is None and line.startswith("# "):  # Headline in markdown
            title_line = line.strip("# ").strip()  # Extract title
        else:
            content_lines.append(line)

    if title_line is None:
        print("No title found in the markdown file.")
        return None, None

    # Sanitize the title to create a valid filename
    sanitized_title = sanitize_filename(title_line)

    # Convert image tags in the content
    content = "\n".join(content_lines)
    content = convert_image_tags(content)

    # Append alias to the frontmatter block
    frontmatter_block.insert(-1, f"alias: {original_filename}")

    # Combine frontmatter and content
    full_content = "\n".join(frontmatter_block) + "\n" + content

    return full_content, sanitized_title


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python convert_md_file.py <filename>")
        sys.exit(1)

    input_file = sys.argv[1]

    if not os.path.isfile(input_file):
        print(f"File not found: {input_file}")
        sys.exit(1)

    # Extract original filename (without .md)
    original_filename = os.path.splitext(os.path.basename(input_file))[0]

    # Read file content from disk
    with open(input_file, "r") as file:
        file_content = file.read()

    # Process the markdown file
    processed_content, new_filename = process_markdown(file_content, original_filename)

    if processed_content and new_filename:
        # Write the processed content to the new file
        new_file_path = f"{new_filename}.md"
        with open(new_file_path, "w") as new_file:
            new_file.write(processed_content)

        print(f"File renamed to: {new_file_path}")
